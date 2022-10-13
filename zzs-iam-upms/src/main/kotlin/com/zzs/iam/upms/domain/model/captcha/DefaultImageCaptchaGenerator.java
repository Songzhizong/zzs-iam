package com.zzs.iam.upms.domain.model.captcha;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

/**
 * @author 宋志宗 on 2022/8/23
 */
public class DefaultImageCaptchaGenerator implements ImageCaptchaGenerator {
  /** 验证码长度 */
  private static final int LENGTH = 4;
  /** 验证码宽度 */
  private static final int WIDTH = 100;
  /** 验证码高度 */
  private static final int HEIGHT = 50;
  private static final int MIN_FOUNT_SIZE = 24;
  private static final int MAX_FOUNT_SIZE = 28;
  private static final Color BG_COLOR = new Color(229, 244, 255);

  @Nonnull
  @Override
  public ImageCaptcha generate() {
    VerifyCode verifyCode = new VerifyCode(
      LENGTH, WIDTH, HEIGHT, BG_COLOR, MAX_FOUNT_SIZE, MIN_FOUNT_SIZE
    );
    BufferedImage image = verifyCode.getImage();
    String text = verifyCode.getText();
    ImageCaptcha captcha = new ImageCaptcha();
    captcha.setImage(image);
    captcha.setCode(text);
    return captcha;
  }

  private static class VerifyCode {
    public static final String VERIFY_CODES = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    /**
     * 列举验证图片中验证码的字体类型
     * {"宋体", "华文楷体", "黑体", "华文新魏", "华文隶书", "微软雅黑", "楷体_GB2312"}
     */
    private static final String[] FONT_NAMES = {"cmmi10"};
    private final SecureRandom random = new SecureRandom();
    private final int length;

    private final int weight;

    private final int height;

    private final Color bgColor;

    private final int minFountSize;

    private final int maxColorSize;

    /**
     * 验证码上的文本
     */
    private String text;

    public VerifyCode(int length, int weight, int height, Color bgColor, int minFountSize, int maxColorSize) {
      this.length = length;
      this.weight = weight;
      this.height = height;
      this.bgColor = bgColor;
      this.minFountSize = minFountSize;
      this.maxColorSize = maxColorSize;
    }

    /**
     * 生成随机的颜色
     */
    @Nonnull
    private Color randomColor() {
      int red = random.nextInt(150);
      int green = random.nextInt(150);
      int blue = random.nextInt(150);
      return new Color(red, green, blue);
    }

    /**
     * 生成随机的字体
     */
    @Nonnull
    @SuppressWarnings("all")
    private Font randomFont() {
      int index = random.nextInt(FONT_NAMES.length);
      // 生成随机的字体名称
      String fontName = FONT_NAMES[index];
      // 生成随机的样式, 0(无样式), 1(粗体), 2(斜体), 3(粗体+斜体)
      int style = random.nextInt(4);
      // 生成随机字号, 24 ~ 28
      int size = random.nextInt(maxColorSize - maxColorSize + 1) + minFountSize;
      return new Font(fontName, style, size);
    }

    /**
     * 画干扰线
     */
    private void drawLine(@Nonnull BufferedImage image) {
      // 一共画3条
      int num = 3;
      Graphics2D g2 = (Graphics2D) image.getGraphics();
      // 生成两个点的坐标，即4个值
      for (int i = 0; i < num; i++) {
        int x1 = random.nextInt(weight);
        int y1 = random.nextInt(height);
        int x2 = random.nextInt(weight);
        int y2 = random.nextInt(height);
        g2.setStroke(new BasicStroke(1.5F));
        // 干扰线是蓝色
        g2.setColor(Color.BLUE);
        // 画线
        g2.drawLine(x1, y1, x2, y2);
      }
    }

    /**
     * 随机生成一个字符
     */
    private char randomChar() {
      int index = random.nextInt(VERIFY_CODES.length());
      return VERIFY_CODES.charAt(index);
    }

    /**
     * 创建BufferedImage
     */
    @Nonnull
    private BufferedImage createImage() {
      // 宽，高，图片的类型
      BufferedImage image = new BufferedImage(weight, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = (Graphics2D) image.getGraphics();
      g2.setColor(bgColor);
      g2.fillRect(0, 0, weight, height);
      return image;
    }

    /**
     * 返回验证码图片上的文本
     */
    public String getText() {
      return text;
    }


    /**
     * 调用这个方法得到验证码
     */
    @Nonnull
    public BufferedImage getImage() {
      // 创建图片缓冲区
      BufferedImage image = createImage();
      // 得到绘制环境
      Graphics2D g2 = (Graphics2D) image.getGraphics();
      // 用来装载生成的验证码文本
      StringBuilder sb = new StringBuilder();
      // 向图片中画4个字符
      // 循环四次，每次生成一个字符
      for (int i = 0; i < length; i++) {
        // 随机生成一个字母
        String s = randomChar() + "";
        sb.append(s);
        // 设置当前字符的x轴坐标
        float x = i * 1.0F * weight / 4;
        // 设置随机字体
        g2.setFont(randomFont());
        // 设置随机颜色
        g2.setColor(randomColor());
        // 画图
        g2.drawString(s, x, height - 5);
      }
      // 把生成的字符串赋给了this.text
      this.text = sb.toString();
      // 添加干扰线
      drawLine(image);
      return image;
    }
  }
}
