public class old{
    public void onEvent(ApplicationEvent event) {
        if (event.getType() == ApplicationEvent.Type.INITIALIZATION_APP_FINISHED) {
            final ImmutableMap.Builder<Method, Timer> timerBuilder = ImmutableMap.<Method, Timer>builder();
            final ImmutableMap.Builder<Method, Meter> meterBuilder = ImmutableMap.<Method, Meter>builder();
            final ImmutableMap.Builder<Method, ExceptionMeterMetric> exceptionMeterBuilder = ImmutableMap.<Method, ExceptionMeterMetric>builder();

            for (final Resource resource : event.getResourceModel().getResources()) {
                for (final ResourceMethod method : resource.getAllMethods()) {
                    registerTimedAnnotations(timerBuilder, method);
                    registerMeteredAnnotations(meterBuilder, method);
                    registerExceptionMeteredAnnotations(exceptionMeterBuilder, method);
                }

                for (final Resource childResource : resource.getChildResources()) {
                    for (final ResourceMethod method : childResource.getAllMethods()) {
                        registerTimedAnnotations(timerBuilder, method);
                        registerMeteredAnnotations(meterBuilder, method);
                        registerExceptionMeteredAnnotations(exceptionMeterBuilder, method);
                    }
                }
            }

            timers = timerBuilder.build();
            meters = meterBuilder.build();
            exceptionMeters = exceptionMeterBuilder.build();
        }
    }
}
