package org.tmt.aps.ics.sidetectorhcd;

import akka.actor.typed.javadsl.ActorContext;
import csw.command.client.messages.TopLevelActorMessage;
import csw.framework.javadsl.JComponentHandlers;
import csw.framework.models.JCswContext;
import csw.location.api.models.TrackingEvent;
import csw.logging.api.javadsl.ILogger;
import csw.params.commands.CommandResponse;
import csw.params.commands.ControlCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import csw.params.commands.Result;
import csw.params.core.generics.Key;
import csw.params.core.generics.Parameter;
import csw.params.core.models.Id;
import csw.params.core.models.ObsId;
import csw.params.core.models.Struct;
import csw.params.javadsl.JKeyType;
import org.tmt.aps.ics.sidetector.api.*;

/**
 * Domain specific logic should be written in below handlers.
 * This handlers gets invoked when component receives messages/commands from other component/entity.
 * For example, if one component sends Submit(Setup(args)) command to SidetectorHcd,
 * This will be first validated in the supervisor and then forwarded to Component TLA which first invokes validateCommand hook
 * and if validation is successful, then onSubmit hook gets invoked.
 * You can find more information on this here : https://tmtsoftware.github.io/csw/commons/framework.html
 */
public class JSidetectorHcdHandlers extends JComponentHandlers {

    private final JCswContext cswCtx;
    private final ILogger log;

    private Map<String, List<ParameterItemInfo>> allParameters;
    private List<StatusItemInfo> allStatuses;
    List<ParameterItemInfo> allCommands;

    int camHandle = -1;

    JSidetectorHcdHandlers(ActorContext<TopLevelActorMessage> ctx,JCswContext cswCtx) {
        super(ctx, cswCtx);
        this.cswCtx = cswCtx;
        this.log = cswCtx.loggerFactory().getLogger(getClass());
    }

    @Override
    public CompletableFuture<Void> jInitialize() {
    log.info("Initializing sidetector HCD...");
    return CompletableFuture.runAsync(() -> {

            try {
                log.info("loading SI Dll");
                SpectralInstrumentsApi.loadDll();

                camHandle = SpectralInstrumentsApi.openCamera("SISIM", "SimCamera");

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("completed initialization");
        });
    }

    @Override
    public CompletableFuture<Void> jOnShutdown() {
        return CompletableFuture.runAsync(() -> {

        });
    }

    @Override
    public void onLocationTrackingEvent(TrackingEvent trackingEvent) {

    }

    @Override
    public CommandResponse.ValidateCommandResponse validateCommand(ControlCommand controlCommand) {
        return new CommandResponse.Accepted(controlCommand.runId());
    }

    @Override
    public CommandResponse.SubmitResponse onSubmit(ControlCommand controlCommand) {

        log.info(controlCommand.commandName().name());
        //prefix
        String prefix = "aps.sidetector";

        switch (controlCommand.commandName().name()) {


            case "getStatusMetaData":

                log.debug("handling point command: " + controlCommand);

                try {
                    Key<String> k1 = JKeyType.StringKey().make("metaData");
                    allStatuses = SpectralInstrumentsApi.getAllStatuses(camHandle);


                    log.info("starting conversion");
                    Parameter<Struct> statusParam = StatusConverter.convertStatusItemListToStatusParam(allStatuses);
                    log.info("ending conversion");

                    //Create Result using madd
                    Result r1 = new Result(prefix).add(statusParam);


                    return new CommandResponse.CompletedWithResult(controlCommand.runId(), r1);

                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "getParameterMetaData":
                log.debug("handling pointDemand command: " + controlCommand);

                try {

                    allParameters = SpectralInstrumentsApi.getAllParameters(camHandle);

                    Key<String> k2 = JKeyType.StringKey().make("metaData");
                    Key<String> listNameKey = JKeyType.StringKey().make("listNameKey");


                    log.info("starting conversion");


                    Result r2 = new Result(prefix);

                    List<Parameter<Struct>> parameterList = new ArrayList<Parameter<Struct>>();
                    for (String paramListName : allParameters.keySet()) {

                        List<ParameterItemInfo> parameterInfoList = allParameters.get(paramListName);

                        Parameter<Struct> paramParameter = ParameterConverter.convertParameterItemListToParameterParam(paramListName, parameterInfoList);

                        parameterList.add(paramParameter);
                        r2 = r2.add(paramParameter);

                    }


                    return new CommandResponse.CompletedWithResult(controlCommand.runId(), r2);

                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "getCommandMetaData":

                log.debug("handling command metadata command: " + controlCommand);

                try {

                    allCommands = SpectralInstrumentsApi.getAllCommands(camHandle);

                    Key<String> k3 = JKeyType.StringKey().make("metaData");


                    log.info("starting conversion");
                    Parameter<Struct> commandParam = ParameterConverter.convertParameterItemListToParameterParam(allCommands);
                    log.info("ending conversion");

                    //Create Result using madd
                    Result r3 = new Result(prefix).add(commandParam);


                    return new CommandResponse.CompletedWithResult(controlCommand.runId(), r3);
                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }

            case "setParameterValue":


                Parameter displayNameParam = controlCommand.paramSet().find(x -> x.keyName().equals("displayName")).get();
                String displayName = (String)displayNameParam.jGet(0).get();

                Parameter valStrParam = controlCommand.paramSet().find(x -> x.keyName().equals("valStr")).get();
                String valStr = (String)valStrParam.jGet(0).get();


                try {
                    SpectralInstrumentsApi.setParameterValue(camHandle, displayName, valStr);
                    return new CommandResponse.Completed(controlCommand.runId());

                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "sendParameters":


                try {
                    SpectralInstrumentsApi.sendParameters(camHandle);
                    return new CommandResponse.Completed(controlCommand.runId());

                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }



            case "getImageSize":


                try {

                    ImageSizeInfo imageSizeInfo = SpectralInstrumentsApi.getImageSize(camHandle);
                    // create a result using imageSizeInfo

                    Key<Integer> serLenKey = JKeyType.IntKey().make("serLen");
                    Key<Integer> parLenKey = JKeyType.IntKey().make("parLen");
                    Key<Integer> is16Key = JKeyType.IntKey().make("is16");
                    Key<Integer> nSerCCDKey = JKeyType.IntKey().make("nSerCCD");
                    Key<Integer> nParCCDKey = JKeyType.IntKey().make("nParCCD");
                    Key<Integer> nSerSectKey = JKeyType.IntKey().make("nSerSect");
                    Key<Integer> nParSectKey = JKeyType.IntKey().make("nParSect");


                    Result r4 = new Result(prefix)
                            .add(serLenKey.set(imageSizeInfo.getSerLen()))
                            .add(parLenKey.set(imageSizeInfo.getParLen()))
                            .add(is16Key.set(imageSizeInfo.getIs16()))
                            .add(nSerCCDKey.set(imageSizeInfo.getnSerCCD()))
                            .add(nParCCDKey.set(imageSizeInfo.getnParCCD()))
                            .add(nSerSectKey.set(imageSizeInfo.getnSerSect()))
                            .add(nParSectKey.set(imageSizeInfo.getnParSect()));

                    return new CommandResponse.CompletedWithResult(controlCommand.runId(), r4);

                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "prepareAcquisition":

                Parameter serLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("serLen")).get();
                int serLen = (Integer)serLenParam.jGet(0).get();

                Parameter parLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("parLen")).get();
                int parLen = (Integer)parLenParam.jGet(0).get();

                Parameter is16Param = controlCommand.paramSet().find(x -> x.keyName().equals("is16")).get();
                int is16 = (Integer)serLenParam.jGet(0).get();


                try {
                    SpectralInstrumentsApi.prepareAcquisition(camHandle, serLen, parLen, is16);
                    return new CommandResponse.Completed(controlCommand.runId());
                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "issueCommand":


                Parameter postNameParam = controlCommand.paramSet().find(x -> x.keyName().equals("postName")).get();
                String postName = (String)postNameParam.jGet(0).get();

                Parameter argStrParam = controlCommand.paramSet().find(x -> x.keyName().equals("argStr")).get();
                String argStr = (String)argStrParam.jGet(0).get();


                try {
                    String commandResponse = SpectralInstrumentsApi.issueCommand(camHandle, postName, argStr);

                    Key<String> issueCommandResponseKey = JKeyType.StringKey().make("issueCommandResponse");
                    Result r4 = new Result(prefix).add(issueCommandResponseKey.set(commandResponse));

                    return new CommandResponse.CompletedWithResult(controlCommand.runId(), r4);


                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "getAcqStatus":

                 try {
                    AcqStatusInfo acqStatusInfo = SpectralInstrumentsApi.getAcqStatus(camHandle);

                    Key<Integer> percentReadKey = JKeyType.IntKey().make("percentRead");
                    Key<Integer> currentFrameKey = JKeyType.IntKey().make("currentFrame");
                    Key<Integer> bufferFlagsKey = JKeyType.IntKey().make("bufferFlags");

                    Result r4 = new Result(prefix)
                            .add(percentReadKey.set(acqStatusInfo.getPercentRead()))
                            .add(currentFrameKey.set(acqStatusInfo.getCurrentFrame()))
                            .add(bufferFlagsKey.set(acqStatusInfo.getBufferFlags()));

                    return new CommandResponse.CompletedWithResult(controlCommand.runId(), r4);


                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }


            case "endAcq":

                Parameter forceAbortParam = controlCommand.paramSet().find(x -> x.keyName().equals("forceAbort")).get();
                boolean forceAbort = (Boolean)forceAbortParam.jGet(0).get();

                serLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("serLen")).get();
                serLen = (Integer)serLenParam.jGet(0).get();

                parLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("parLen")).get();
                parLen = (Integer)parLenParam.jGet(0).get();

                // the image buffer and arrayLen are not passed in, as these are handled differently, using the VBDS
                int arrayLen = serLen * parLen;
                int[] imageBuffer = new int[arrayLen];


                try {
                    SpectralInstrumentsApi.endAcq(camHandle, forceAbort, imageBuffer, arrayLen);

                    // TODO: do something using VBDS


                    return new CommandResponse.Completed(controlCommand.runId());
                } catch (Exception e) {
                    return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
                }



            default:
                log.error("unhandled message in Monitor Actor onMessage: " + controlCommand);
                // maintain actor state

                return new CommandResponse.Error(controlCommand.runId(), "command not recognized");

        }
    }

    @Override
    public void onOneway(ControlCommand controlCommand) {

    }

    @Override
    public void onGoOffline() {

    }

    @Override
    public void onGoOnline() {

    }
}
