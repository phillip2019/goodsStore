package cn.bingoogolapple.qrcode.zxingdemo.DTO;

import java.io.Serializable;

public class GoodsDTO implements Serializable {

    // 编号
    private String id;

    // 类别
    private String category;

    // 名字
    private String name;

    // 商品库存数量
    private Integer counts;

    // 批发价
    private Double tradePrice;

    // 建议零售价1
    private Double retailPrice1;

    // 建议零售价2
    private Double retailPrice2;

    // 建议零售价3
    private Double retailPrice3;

    // 图像存储路径
    private String imagePath;

    // 备注
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Double getRetailPrice1() {
        return retailPrice1;
    }

    public void setRetailPrice1(Double retailPrice1) {
        this.retailPrice1 = retailPrice1;
    }

    public Double getRetailPrice2() {
        return retailPrice2;
    }

    public void setRetailPrice2(Double retailPrice2) {
        this.retailPrice2 = retailPrice2;
    }

    public Double getRetailPrice3() {
        return retailPrice3;
    }

    public void setRetailPrice3(Double retailPrice3) {
        this.retailPrice3 = retailPrice3;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    /**
     * 得到简短商品编号
     * @param id
     * @return
     */
    public static String getShortID(String id) {
        // 提取商品id最后4位商品编号
        return id.substring(id.length() - 5, id.length() - 1);
    }
}
