package gr.cite.rda.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(
        scanBasePackages = {
                "gr.cite.rda.*",
                "gr.cite",
                "gr.cite.queueoutbox",
                "gr.cite.queueinbox",
                "gr.cite.rda.integrationevent",
                "gr.cite.tools",
                "gr.cite.commons"})
@EntityScan({
        "gr.cite.rda.data"})
@EnableAsync
public class RdaMadmpApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RdaMadmpApiApplication.class, args);
    }

}
