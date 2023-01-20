package hu.codeandsoda.osa.security.data;

import java.util.Map;

public class AuthenticationFailureData {

    private Map<String, String> errors;

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

}
