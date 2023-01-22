package com.anf.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * Simple JUnit test verifying the HelloWorldModel
 *
 */
@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

    private final AemContext aemContext = new AemContext();
    NewsFeedModel model = new NewsFeedModel();

    @BeforeEach
    public void setUp() throws Exception {
        aemContext.addModelsForClasses(NewsFeedModel.class);
        aemContext.load().json("news.json", "/var/commerce/products/anf-code-challenge");
        model = aemContext.currentResource("/var/commerce/products/anf-code-challenge/newsData").adaptTo(NewsFeedModel.class);
    }

    @Test
    void testNewsItemsList() {
       aemContext.currentResource("/var/commerce/products/anf-code-challenge/newsData");
       assertEquals(2, model.getNewsItemsList().size());
    }

}