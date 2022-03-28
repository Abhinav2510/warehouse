package com.myhomeshop.inventory.warehouse.dataloaders;

import com.myhomeshop.inventory.warehouse.repos.InventoryArticleRepo;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(
        properties = {"camel.springboot.name=customName"}
)
@DirtiesContext
public class InventoryArticleLoaderRouteTest {
    @Autowired
    ProducerTemplate producerTemplate;
    @Value("${inventory.file.path}")
    private String inventoryLoadFilePath;
    @Value("${inventory.file.name}")
    private String inventoryLoadFileName;

    @Autowired
    private InventoryArticleRepo articleRepo;


    @EndpointInject("mock:result")
    private MockEndpoint mockOutput;


    @Autowired
    CamelContext context;

    @BeforeEach
    public void init() throws Exception {
        AdviceWith.adviceWith(context, "articleLoaderRoute", a ->
                a.weaveAddLast().to("mock:result")
        );

    }

    @Test
    public void shouldAutowireProducerTemplate() {
        assertNotNull(producerTemplate);
    }

    @Test
    public void shouldLoad4Products() throws Exception {

        mockOutput.expectedMessageCount(1);

        String input = String.join("", Files.readAllLines(new File(getClass().getResource("/data/inventory.json").getFile()).toPath()));

        producerTemplate.sendBodyAndProperty(LoaderUtils.getFileEndPointFromPath(inventoryLoadFilePath, inventoryLoadFileName), input, Exchange.FILE_NAME, "inventory.json");

        mockOutput.assertIsSatisfied(500000);
        assertEquals(4, articleRepo.count());

    }


}
