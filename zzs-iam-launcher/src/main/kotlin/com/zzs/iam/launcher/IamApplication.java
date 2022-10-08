package com.zzs.iam.launcher;

import com.zzs.iam.common.password.IamPasswordEncoder;
import com.zzs.iam.common.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/**
 * @author 宋志宗 on 2022/9/5
 */
@EnableReactiveMongoAuditing
@SpringBootApplication(proxyBeanMethods = false)
public class IamApplication {
  private static final Logger log = LoggerFactory.getLogger(IamApplication.class);

  public static void main(String[] args) {
    int ioWorkerCount = Runtime.getRuntime().availableProcessors();
    System.setProperty("reactor.netty.ioWorkerCount", String.valueOf(ioWorkerCount));
    System.setProperty("reactor.netty.pool.leasingStrategy", "lifo");
    SpringApplication.run(IamApplication.class, args);
    log.info("io worker threads: {}", ioWorkerCount);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new IamPasswordEncoder();
  }
}
