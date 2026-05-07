package networkDTO;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType type;
    private Object data;
    private String errorMessage;

    public Response(ResponseType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static Response ok(Object data) {
        return new Response(ResponseType.OK, data);
    }

    public static Response error(String message) {
        Response r = new Response(ResponseType.ERROR, null);
        r.errorMessage = message;
        return r;
    }

    // trimis de server catre toti clientii cand se schimba datele
    public static Response update(Object data) {
        return new Response(ResponseType.UPDATE_CURSE, data);
    }

    public ResponseType getType() { return type; }
    public Object getData() { return data; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public String toString() {
        return "Response{type=" + type + ", data=" + data + ", error=" + errorMessage + '}';
    }
}