package com.example;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@ConfigurationProperties(prefix = "serviceBus")
public class ServiceBusProperties {
    private String clientId;
    private String scheme = "amqps";
    private String host;
    private String sharedAccessPolicyName;
    private String sharedAccessPolicyKey;

    public String getUrlString() throws UnsupportedEncodingException {
        return String.format("%1s://%2s?amqp.idleTimeout=3600000&amqp.traceFrames=true", scheme, host);
    }

    public String getNamespace() {
        return host.split("\\.")[0];
    }

    public String getRootUri() {
        String[] split = host.split("\\.");
        return "." + StringUtils.join(Arrays.copyOfRange(split, 1, split.length), '.');
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSharedAccessPolicyName() {
        return sharedAccessPolicyName;
    }

    public void setSharedAccessPolicyName(String sharedAccessPolicyName) {
        this.sharedAccessPolicyName = sharedAccessPolicyName;
    }

    public String getSharedAccessPolicyKey() {
        return sharedAccessPolicyKey;
    }

    public void setSharedAccessPolicyKey(String sharedAccessPolicyKey) {
        this.sharedAccessPolicyKey = sharedAccessPolicyKey;
    }
}
