package ro.andrei.reverseproxy.util;

public class Constants {

    public static final String HOST_HEADER = "Host";
    public static final String ERROR_MESSAGE = "Exception occurred. Please contact your system admin for more information";
    public static final String GENERAL_EXCEPTION_MESSAGE = "Request /{} {} failed with unknown reasons. Dropping request!\n{}";
    public static final String UNREGISTERED_HOST_EXCEPTION_MESSAGE = "Request /{} {} has an unknown host. Dropping request!";
    public static final String REST_EXCEPTION_MESSAGE = "Request /{} {} has failed due to a REST exception. The host might be down!";
}
