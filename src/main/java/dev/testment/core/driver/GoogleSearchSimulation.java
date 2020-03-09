package dev.testment.core.driver;

import dev.testment.core.logger.Logger;
import dev.testment.core.logger.LoggerFactory;
import dev.testment.core.selenium.simulation.SeleniumSimulation;
import dev.testment.core.simulation.Simulation;
import dev.testment.core.simulation.action.Action;
import dev.testment.core.simulation.param.Param;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

@Simulation(value = "Search", actionPrefix = "GS", description = "Google Search Simulation")
public class GoogleSearchSimulation extends SeleniumSimulation {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchSimulation.class);

    @Param private String url;
    @Param private String query;

    @Action
    public void hitServer() {
        logger.info("Hitting google");
        driver.get(url);
    }

    @Action
    public void search() {
        logger.info("Performing web search");
        WebElement q = driver.findElement(By.name("q"));
        q.sendKeys(query);
        q.sendKeys(Keys.ENTER);
    }

    @Action
    public void clickImages() {
        logger.info("Clicking images");
        driver.findElement(By.xpath("//a[contains(text(), 'Images')]")).click();
    }

}