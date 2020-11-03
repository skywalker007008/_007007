package warn_relation;

public class RelatedWarning {
    private WarnChain chain;

    private int chain_warn_type;

    private int id;

    private int length;

    public RelatedWarning(int id, WarnChain chain) {
        this.id = id;
        this.chain = chain;
    }

    public int FindChainWarnType() {
        return 0;
    }
}
