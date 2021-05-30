package org.icpclive.webadmin.mainscreen.properties;

import java.util.Properties;

public class CreepingLineProperties {
    private final int maximumFlowSize;
    private final long messageLifespanCreepingLine;


    public CreepingLineProperties(final Properties properties) {
        this.maximumFlowSize = Integer.parseInt(properties.getProperty("creeping.line.maximum.flow", "30"));
        this.messageLifespanCreepingLine = Integer.parseInt(properties.getProperty("creeping.line.message.lifespan", "600000"));
    }


    public long getMessageLifespanCreepingLine() {
        return messageLifespanCreepingLine;
    }

    public int getMaximumFlowSize() {
        return maximumFlowSize;
    }
}
