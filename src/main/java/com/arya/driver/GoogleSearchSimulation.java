package com.arya.driver;

import com.arya.logger.Logger;
import com.arya.logger.LoggerFactory;
import com.arya.selenium.SeleniumSimulation;
import com.arya.simulation.Action;
import com.arya.simulation.Param;
import com.arya.simulation.Simulation;
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