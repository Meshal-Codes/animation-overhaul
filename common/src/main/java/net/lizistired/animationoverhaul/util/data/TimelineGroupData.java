package net.lizistired.animationoverhaul.util.data;

import com.google.common.collect.Maps;
import net.lizistired.animationoverhaul.util.time.ChannelTimeline;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class TimelineGroupData {

    public static TimelineGroupData INSTANCE = new TimelineGroupData();

    //TODO: Set this as a class,

    // Entity type entries -> Specific animation entries -> Part entries -> Timeline
    private final Map<Identifier, TimelineGroup> animationEntries = Maps.newHashMap();

    public TimelineGroupData(){
    }

    public void put(Identifier resourceLocation, TimelineGroup timelineGroup){
        animationEntries.put(resourceLocation, timelineGroup);
    }

    public TimelineGroup get(Identifier resourceLocation){
        if(animationEntries.containsKey(resourceLocation)){
            return animationEntries.get(resourceLocation);
        } else {
            //AnimationOverhaulMain.LOGGER.error("Resource location {} not found in loaded data", resourceLocation);
            return TimelineGroup.blank();
        }
    }

    public TimelineGroup get(String namespace, String path){
        return get(new Identifier(namespace, path));
    }

    public TimelineGroup get(String namespace, EntityType<?> entityType, String animationKey){
        return get(namespace, entityType.getUntranslatedName() + "/" + animationKey);
    }

    public void clearAndReplace(TimelineGroupData newAnimationData){
        this.animationEntries.clear();
        for(Identifier resourceLocation : newAnimationData.getHashMap().keySet()){
            this.put(resourceLocation, newAnimationData.get(resourceLocation));
        }
    }

    public Map<Identifier, TimelineGroup> getHashMap(){
        return animationEntries;
    }

    public static class TimelineGroup {

        private Map<String, ChannelTimeline> partTimelines = Maps.newHashMap();
        private final float frameLength;

        public TimelineGroup(float timelineFrameLength){
            frameLength = timelineFrameLength;
        }

        public ChannelTimeline getPartTimeline(String partName){
            return partTimelines.get(partName);
        }

        public float getFrameLength(){
            return frameLength;
        }

        public boolean containsPart(String partName){
            return partTimelines.containsKey(partName);
        }

        public void addPartTimeline(String partName, ChannelTimeline partTimeline){
            partTimelines.put(partName, partTimeline);
        }

        public static TimelineGroup blank(){
            return new TimelineGroup(10);
        }
    }
}
