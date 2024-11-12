package net.media.autotemplate.bean;

import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.constants.CreativeConstants;

import java.util.ArrayList;
import java.util.List;

/*
    Created by shubham-ar
    on 12/2/18 11:23 PM   
*/
public class TemplateStatus {
    List<String> enable;
    List<String> disable;
    String buSelected;

    public TemplateStatus(List<String> enable, List<String> disable, String buSelected) {
        this.enable = enable;
        this.disable = disable;
        this.buSelected = buSelected;
    }

    public void setDisable(List<String> disable) {
        this.disable = disable;
    }

    public void setEnable(List<String> enable) {
        this.enable = enable;
    }

    public String getBuSelected() {
        return buSelected;
    }

    public List<String> getTemplatesToEnable() {
        return enable;
    }

    public List<String> getTemplatesToDisable() {
        return disable;
    }

    public List<BlockingInfo> getBlockingInfoList(Entity entity) {
        List<BlockingInfo> blockingInfos = new ArrayList<>();
        for (String en : enable) {
            blockingInfos.add(new BlockingInfo(entity.getEntityId() + "", en, CreativeConstants.Type.TEMPLATE, entity.getSize(), CreativeConstants.Status.W, CreativeConstants.Level.ENTITY));
        }
        for (String dis : disable) {
            blockingInfos.add(new BlockingInfo(entity.getEntityId() + "", dis, CreativeConstants.Type.TEMPLATE, entity.getSize(), CreativeConstants.Status.B, CreativeConstants.Level.ENTITY));
        }

        return blockingInfos;
    }
}
