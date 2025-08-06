package com.ruoyi.redpacket.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图片资源对象 redpacket_image_resource
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public class RedpacketImageResource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 资源ID */
    private Long id;

    /** 资源名称 */
    @Excel(name = "资源名称")
    private String resourceName;

    /** 资源标识 */
    @Excel(name = "资源标识")
    private String resourceKey;

    /** 文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 文件访问路径 */
    private String filePath;

    /** 使用场景 */
    @Excel(name = "使用场景")
    private String usageScene;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setResourceName(String resourceName) 
    {
        this.resourceName = resourceName;
    }

    public String getResourceName() 
    {
        return resourceName;
    }

    public void setResourceKey(String resourceKey) 
    {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() 
    {
        return resourceKey;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setUsageScene(String usageScene) 
    {
        this.usageScene = usageScene;
    }

    public String getUsageScene() 
    {
        return usageScene;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("resourceName", getResourceName())
            .append("resourceKey", getResourceKey())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("usageScene", getUsageScene())
            .append("description", getDescription())
            .toString();
    }
}
