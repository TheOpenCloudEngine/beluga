package org.opencloudengine.garuda.controller;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:org/opencloudengine/garuda/controller/spring/dispatcher-servlet.xml",
	"classpath:org/opencloudengine/garuda/controller/spring/datasource-context.xml",
})
@WebAppConfiguration
@ActiveProfiles(profiles = "prd")
@Ignore
public class TestParent {
}
