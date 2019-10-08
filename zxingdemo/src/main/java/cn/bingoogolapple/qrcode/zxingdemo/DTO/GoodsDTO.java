package cn.bingoogolapple.qrcode.zxingdemo.DTO;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class GoodsDTO extends LitePalSupport implements Serializable {

    // 编号
    @Column(unique = true, nullable = false)
    private String num;

    // 货号
    @Column(unique = false, nullable = false)
    private String shortNum;

    // 类别
    @Column()
    private String category;

    // 名字
    @Column()
    private String name;

    // 商品库存数量
    @Column(defaultValue = "0")
    private Integer counts;

    // 批发价
    @Column(defaultValue = "0.0")
    private Double tradePrice;

    // 建议零售价1
    @Column(defaultValue = "0.0")
    private Double retailPrice1;

    // 建议零售价2
    @Column(defaultValue = "0.0")
    private Double retailPrice2;

    // 建议零售价3
    @Column(defaultValue = "0.0")
    private Double retailPrice3;

    // 图像存储路径
    @Column()
    private String imagePath;

    // 备注
    @Column()
    private String remark;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getShortNum() {
        return shortNum;
    }

    public void setShortNum(String shortNum) {
        this.shortNum = shortNum;
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
     * 简短商品编号
     * @param num
     * @return
     */
    public static String defaultShortNum(String num) {
        // 提取商品id最后4位商品编号
        return num.substring(num.length() - 5, num.length() - 1);
    }
}
