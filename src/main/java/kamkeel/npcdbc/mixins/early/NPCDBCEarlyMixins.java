package kamkeel.npcdbc.mixins.early;


import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NPCDBCEarlyMixins implements IMixinConfigPlugin {

    public static final MixinEnvironment.Side side = MixinEnvironment.getCurrentEnvironment().getSide();

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        ArrayList<String> list = new ArrayList<>();

        if (side == MixinEnvironment.Side.CLIENT) {
            list.add("client.MixinSoundManager");
        }

        return list;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
