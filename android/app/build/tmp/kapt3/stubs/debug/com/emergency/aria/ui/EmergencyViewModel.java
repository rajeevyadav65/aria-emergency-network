package com.emergency.aria.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/emergency/aria/ui/EmergencyViewModel;", "Landroidx/lifecycle/ViewModel;", "communicationManager", "Lcom/emergency/aria/CommunicationManager;", "(Lcom/emergency/aria/CommunicationManager;)V", "sendEmergency", "", "lat", "", "lon", "triggeredBy", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class EmergencyViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.CommunicationManager communicationManager = null;
    
    @javax.inject.Inject
    public EmergencyViewModel(@org.jetbrains.annotations.NotNull
    com.emergency.aria.CommunicationManager communicationManager) {
        super();
    }
    
    public final void sendEmergency(double lat, double lon, @org.jetbrains.annotations.NotNull
    java.lang.String triggeredBy) {
    }
}