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

                int camHandle = SpectralInstrumentsApi.openCamera("SISIM", "SimCamera");
                allParameters = SpectralInstrumentsApi.getAllParameters(camHandle);
                allStatuses = SpectralInstrumentsApi.getAllStatuses(camHandle);

                allCommands = SpectralInstrumentsApi.getAllCommands(camHandle);

                log.info(""+allCommands);

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

                Key<String> k1 = JKeyType.StringKey().make("metaData");


                log.info("starting conversion");
                Parameter<Struct> statusParam = StatusConverter.convertStatusItemListToStatusParam(allStatuses);
                log.info("ending conversion");

                //Create Result using madd
                Result r1 = new Result(prefix).add(statusParam);


                return new CommandResponse.CompletedWithResult(controlCommand.runId(), r1);


            case "getParameterMetaData":
                log.debug("handling pointDemand command: " + controlCommand);

                log.debug("handling point command: " + controlCommand);

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


            case "getCommandMetaData":

                log.debug("handling command metadata command: " + controlCommand);

                Key<String> k3 = JKeyType.StringKey().make("metaData");


                log.info("starting conversion");
                Parameter<Struct> commandParam = ParameterConverter.convertParameterItemListToParameterParam(allCommands);
                log.info("ending conversion");

                //Create Result using madd
                Result r3 = new Result(prefix).add(commandParam);


                return new CommandResponse.CompletedWithResult(controlCommand.runId(), r3);



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
