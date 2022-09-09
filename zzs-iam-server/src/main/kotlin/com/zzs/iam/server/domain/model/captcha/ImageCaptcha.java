package com.zzs.iam.server.domain.model.captcha;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.Transient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author 宋志宗 on 2022/8/23
 */
public class ImageCaptcha extends Captcha {
  /** 图形验证码生成器 */
  private static ImageCaptchaGenerator generator = new DefaultImageCaptchaGenerator();
  private transient volatile String base64 = null;
  private transient BufferedImage image;

  /** 生成图片验证码 */
  @Nonnull
  public static ImageCaptcha generate() {
    return generator.generate();
  }

  public static void setGenerator(@Nonnull ImageCaptchaGenerator generator) {
    ImageCaptcha.generator = generator;
  }

  @Transient
  public String getBase64() throws IOException {
    if (base64 == null) {
      synchronized (this) {
        if (base64 == null) {
          try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(getImage(), "JPEG", stream);
            Base64.Encoder encoder = Base64.getEncoder();
            base64 = encoder.encodeToString(stream.toByteArray());
          }
        }
      }
    }
    return base64;
  }

  @Transient
  public String getTypedBase64() throws IOException {
    String base64 = getBase64();
    return "data:image/jpeg;base64," + base64;
  }

  public void setBase64(String base64) {
    this.base64 = base64;
  }

  @Transient
  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }
}
