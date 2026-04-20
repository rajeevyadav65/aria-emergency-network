package com.emergency.aria.ui;

@dagger.hilt.android.AndroidEntryPoint
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001a\u001a\u00020\u0019H\u0002J\u0012\u0010\u001b\u001a\u00020\u00192\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0014J\b\u0010\u001e\u001a\u00020\u0019H\u0014J$\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#2\b\u0010$\u001a\u0004\u0018\u00010%H\u0016J\u001c\u0010&\u001a\u00020\u00192\b\u0010\'\u001a\u0004\u0018\u00010#2\b\u0010$\u001a\u0004\u0018\u00010%H\u0016J\b\u0010(\u001a\u00020\u0019H\u0002J\u0016\u0010)\u001a\u00020\u00192\u0006\u0010*\u001a\u00020\r2\u0006\u0010+\u001a\u00020!J\u0010\u0010,\u001a\u00020\u00192\u0006\u0010-\u001a\u00020#H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/emergency/aria/ui/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/razorpay/PaymentResultWithDataListener;", "()V", "btnSos", "Lcom/google/android/material/button/MaterialButton;", "commManager", "Lcom/emergency/aria/CommunicationManager;", "getCommManager", "()Lcom/emergency/aria/CommunicationManager;", "setCommManager", "(Lcom/emergency/aria/CommunicationManager;)V", "currentDoctorId", "", "emergencyDialog", "Landroidx/appcompat/app/AlertDialog;", "fallDetector", "Lcom/emergency/aria/utils/FallDetector;", "locationHelper", "Lcom/emergency/aria/utils/LocationHelper;", "timerJob", "Lkotlinx/coroutines/Job;", "voiceHelper", "Lcom/emergency/aria/utils/VoiceTriggerHelper;", "checkAndAskLanguagePreference", "", "handleImpactDetected", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onPaymentError", "errorCode", "", "response", "", "paymentData", "Lcom/razorpay/PaymentData;", "onPaymentSuccess", "razorpayPaymentId", "showEmergencyPopup", "startDoctorAppointmentPayment", "doctorId", "doctorFee", "triggerEmergencyProtocol", "reason", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity implements com.razorpay.PaymentResultWithDataListener {
    @javax.inject.Inject
    public com.emergency.aria.CommunicationManager commManager;
    private com.google.android.material.button.MaterialButton btnSos;
    private com.emergency.aria.utils.LocationHelper locationHelper;
    private com.emergency.aria.utils.FallDetector fallDetector;
    private com.emergency.aria.utils.VoiceTriggerHelper voiceHelper;
    @org.jetbrains.annotations.Nullable
    private androidx.appcompat.app.AlertDialog emergencyDialog;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job timerJob;
    private long currentDoctorId = 0L;
    
    public MainActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.emergency.aria.CommunicationManager getCommManager() {
        return null;
    }
    
    public final void setCommManager(@org.jetbrains.annotations.NotNull
    com.emergency.aria.CommunicationManager p0) {
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void checkAndAskLanguagePreference() {
    }
    
    public final void startDoctorAppointmentPayment(long doctorId, int doctorFee) {
    }
    
    @java.lang.Override
    public void onPaymentSuccess(@org.jetbrains.annotations.Nullable
    java.lang.String razorpayPaymentId, @org.jetbrains.annotations.Nullable
    com.razorpay.PaymentData paymentData) {
    }
    
    @java.lang.Override
    public void onPaymentError(int errorCode, @org.jetbrains.annotations.Nullable
    java.lang.String response, @org.jetbrains.annotations.Nullable
    com.razorpay.PaymentData paymentData) {
    }
    
    private final void handleImpactDetected() {
    }
    
    private final void showEmergencyPopup() {
    }
    
    private final void triggerEmergencyProtocol(java.lang.String reason) {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
}