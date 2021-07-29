

/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.contrib.minibus;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.minibus.PConfigGroup.PVehicleSettings;
import org.matsim.contrib.minibus.hook.PModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.population.algorithms.TripsToLegsAlgorithm;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.PtConstants;

import java.util.Collections;

import static org.matsim.core.config.groups.PlansConfigGroup.HandlingOfPlansWithoutRoutingMode.useMainModeIdentifier;


/**
 * Entry point, registers all necessary hooks
 *
 * @author aneumann
 */
public final class RunMinibus {

	private final static Logger log = Logger.getLogger(RunMinibus.class);

//	private final Config config ;

//	public RunMinibus( final String [] args ) {
////		if(args.length == 0){
////			log.info("Arg 1: config.xml is missing.");
////			log.info("Check http://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/atlantis/minibus/ for an example.");
////			System.exit(1);
////		}
//		config = ConfigUtils.loadConfig( args[0], new PConfigGroup() ) ;
//	}
//
//	public final void run() {
//		Scenario scenario = ScenarioUtils.loadScenario(config);
//
//		Controler controler = new Controler(scenario);
//
//		controler.addOverridingModule(new PModule()) ;
//
//		controler.run();
//	}
//
//	public final Config getConfig() {
//		return this.config ;
//	}
//
//	public static void main(final String[] args) {
//		new RunMinibus( args ).run() ;
//	}




	public static void main(final String[] args) {
		Config config = ConfigUtils.loadConfig( "/Users/MeyerMa/Desktop/MA/scenarios/berlin/input/config/config_vehicle_types.xml", new PConfigGroup() ) ;
//		Config config = ConfigUtils.loadConfig("/Users/MeyerMa/IdeaProjects/minibus_meyer/Input/config.xml", new PConfigGroup() ) ;
		config.network().setInputFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_network.xml.gz");
		config.global().setCoordinateSystem("EPSG:31468");
		config.global().setRandomSeed(1);
		config.plans().setInputFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.4-1pct.plans_activity_inside_prep.xml");
		config.plans().setRemovingUnneccessaryPlanAttributes(true);
		config.transit().setTransitScheduleFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_transitSchedule_no_bus_in_spandau.xml.gz");
		config.transit().setVehiclesFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_transitVehicles.xml.gz");
//		config.households().setInputFile();
//		config.facilities().setInputFile();

		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory("/Users/MeyerMa/Desktop/MA/scenarios/berlin/output/minibus_human_600it_seed1");

		config.plans().setHandlingOfPlansWithoutRoutingMode(useMainModeIdentifier);
		Scenario scenario = ScenarioUtils.loadScenario(config);
		config.controler().setRunId("seed_1");
		config.controler().setLastIteration(400);





		MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();
		TripsToLegsAlgorithm algorithm = new TripsToLegsAlgorithm(mainModeIdentifier);



		for (Person person: scenario.getPopulation().getPersons().values())	{
			Plan plan = person.getSelectedPlan();
			algorithm.run(plan);
			for (PlanElement element : plan.getPlanElements()) {
				if (element instanceof Activity) {
					Activity activity = (Activity) element;
					if (!Collections.singleton(PtConstants.TRANSIT_ACTIVITY_TYPE).contains(activity.getType())) {
						activity.setType("h");
					}
				}
			}
		}



		Controler controler = new Controler(scenario);

		controler.addOverridingModule(new PModule());



		// if desired, add subsidy approach here
		PConfigGroup pConfig = ConfigUtils.addOrGetModule(config, PConfigGroup.class);
		//pConfig.setSubsidyApproach("perPassenger");
		//Id<PConfigGroup.PVehicleSettings> id = null;
//		pConfig.getPVehicleSettings(null, true)
//				.setPVehicleName("")
//				.setCapacityPerVehicle(3)
//				.setEarningsPerBoardingPassenger(0)
//				.setEarningsPerKilometerAndPassenger(.55)
//				.setCostPerVehicleAndDay(2)
//				.setCostPerKilometer(1)
//				.setCostPerHour(1)
//				.setCostPerVehicleSold(1)
//				.setCostPerVehicleBought(1);
//
//		pConfig.getPVehicleSettings(null, true)
//				.setPVehicleName("")
//				.setEarningsPerBoardingPassenger(0)
//				.setEarningsPerKilometerAndPassenger(.55)
//				.setCostPerVehicleAndDay()
//				.setCostPerKilometer()
//				.setCostPerHour()
//				.setCostPerVehicleSold()
//				.setCostPerVehicleBought();
//
//		pConfig.getPVehicleSettings(null, true)
//				.setPVehicleName()
//				.setCapacityPerVehicle(15)
//				.setEarningsPerBoardingPassenger(0)
//				.setEarningsPerKilometerAndPassenger(.55)
//				.setCostPerVehicleAndDay()
//				.setCostPerKilometer()
//				.setCostPerHour()
//				.setCostPerVehicleSold()
//				.setCostPerVehicleBought();
















//		PConfigGroup.PVehicleSettings pvs1=new PConfigGroup.PVehicleSettings(id);
//		pvs1.setPVehicleName("minibus");
//		//pvs1.setId((Id<PConfigGroup.PVehicleSettings>)1);
//		pvs1.setCapacityPerVehicle(12);
//		pvs1.setCostPerHour();

		//pConfig.getPVehicleSettings().add(pvs1);

		controler.run();
	}

}
