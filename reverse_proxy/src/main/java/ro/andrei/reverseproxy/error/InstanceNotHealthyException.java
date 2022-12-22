package ro.andrei.reverseproxy.error;

import ro.andrei.reverseproxy.model.HostInfo;

public class InstanceNotHealthyException extends RuntimeException {

    private final String name;
    private final HostInfo instance;

    public InstanceNotHealthyException(String name, HostInfo instance) {
        this.name = name;
        this.instance = instance;
    }
}
