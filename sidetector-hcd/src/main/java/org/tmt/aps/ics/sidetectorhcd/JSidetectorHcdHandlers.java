package org.tmt.aps.ics.sidetectorhcd;

import akka.actor.typed.javadsl.ActorContext;
import csw.command.client.messages.TopLevelActorMessage;
import csw.framework.javadsl.JComponentHandlers;
import csw.framework.models.JCswContext;
import csw.location.api.models.TrackingEvent;
import csw.logging.api.javadsl.ILogger;
import csw.params.commands.CommandResponse;
import csw.params.commands.ControlCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
//import org.tmt.aps.ics.sidetector.api.*;

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

    //private Map<String, List<ParameterItemInfo>> allParameters;
    //private List<StatusItemInfo> allStatuses;

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
                //SpectralInstrumentsApi.loadDll();

                //int camHandle = SpectralInstrumentsApi.openCamera("SISIM", "SimCamera");
                //allParameters = SpectralInstrumentsApi.getAllParameters(camHandle);
                //allStatuses = SpectralInstrumentsApi.getAllStatuses(camHandle);
                //String commands = SpectralInstrumentsApi.getXmlFile(camHandle, "command.xml");

                //log.info("commands = " + commands);

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
        return new CommandResponse.Completed(controlCommand.runId());
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
