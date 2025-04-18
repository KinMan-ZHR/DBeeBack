package kinman.dbee.api.enums;

public enum NodeCompileTypeEnum {

    NPM(1, "npm"),
    PNPM(2, "pnpm"),
    YARN(3, "yarn");

    private final Integer code;

    private final String value;

    private NodeCompileTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
