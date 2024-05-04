package kamkeel.npcdbc.data;

public class FuseRequest {

    private String requester;
    private String target;
    private String hash;
    private boolean rightSide;
    private int tier;
    private final long requestTime = System.currentTimeMillis();

    public FuseRequest(String requester, String target, boolean right, String hash, int tier) {
        this.requester = requester;
        this.target = target;
        this.rightSide = right;
        this.hash = hash;
        this.tier = tier;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public boolean checkRequest(FuseRequest target){
        if(this.rightSide == target.rightSide)
            return false;
        if(!this.hash.equals(target.hash))
            return false;
        if(this.tier != target.tier)
            return false;
        if(!target.target.equals(this.requester))
            return false;
        if(!target.requester.equals(this.target))
            return false;
        return System.currentTimeMillis() < target.getRequestTime() + 5000L;
    }

    public boolean newRequest(FuseRequest old){
        if(this.rightSide != old.rightSide)
            return true;
        if(!this.hash.equals(old.hash))
            return true;
        if(this.tier != old.tier)
            return true;
        if(!old.requester.equals(this.requester))
            return true;
        if(!old.target.equals(this.target))
            return true;
        return System.currentTimeMillis() > old.getRequestTime() + 5000L;
    }
}
