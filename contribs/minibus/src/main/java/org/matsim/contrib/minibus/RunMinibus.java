

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
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.minibus.PConfigGroup;
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


		// input file

		Config config = ConfigUtils.loadConfig( "/Users/MeyerMa/Desktop/MA/scenarios/berlin/input/config/config_vehicle_types.xml", new PConfigGroup() ) ;
//		Config config = ConfigUtils.loadConfig("/Users/MeyerMa/IdeaProjects/minibus_meyer/Input/config.xml", new PConfigGroup() ) ;

		config.network().setInputFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_network.xml.gz");
		config.global().setCoordinateSystem("EPSG:31468");
		config.global().setRandomSeed(2);
		config.global().setNumberOfThreads(8);


		config.plans().setInputFile("/Users/MeyerMa/Desktop/MA/scenarios/berlin/input/v5.4/v5.4_1pct/berlin-v5.4-1pct.plans_activity_inside_prep_test.xml.gz");
		config.plans().setRemovingUnneccessaryPlanAttributes(true);
		config.plans().setHandlingOfPlansWithoutRoutingMode(useMainModeIdentifier);
		config.plans().setNetworkRouteType("LinkNetworkRoute");

		MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();
		TripsToLegsAlgorithm algorithm = new TripsToLegsAlgorithm(mainModeIdentifier);








		config.transit().setTransitScheduleFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_transitSchedule_no_bus_in_spandau.xml.gz");
		config.transit().setVehiclesFile("/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_transitVehicles.xml.gz");


		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory("/Users/MeyerMa/Desktop/MA/scenarios/berlin/output/subsidy/subsidy_vehicle_types_test");
		config.controler().setRunId("per_passenger_1");
		config.controler().setLastIteration(400);
		config.controler().setWriteEventsInterval(400);
		config.controler().setWritePlansInterval(400);
		//config.controler().setRoutingAlgorithmType(""); manser used Dijlstra and i use a star landmarks

		config.planCalcScore().setBrainExpBeta(1);
		config.planCalcScore().setPathSizeLogitBeta(1);
		config.planCalcScore().setFractionOfIterationsToStartScoreMSA(null);
		config.planCalcScore().setLearningRate(1);
		config.planCalcScore().setUsingOldScoringBelowZeroUtilityDuration(false);


		config.plansCalcRoute().setNetworkModes(Collections.singleton("car"));
		config.plansCalcRoute().setRoutingRandomness(3);



		config.qsim().setFlowCapFactor(0.1);
		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(false);
		config.qsim().setNumberOfThreads(8);


//		<!-- additional time the router allocates when a line switch happens. Can be interpreted as a 'safety' time that agents need to safely transfer from one line to another -->
		config.transitRouter().setAdditionalTransferTime(0);
		//		<!-- Factor with which direct walk generalized cost is multiplied before it is compared to the pt generalized cost.  Set to a very high value to reduce direct walk results. -->
		config.transitRouter().setDirectWalkFactor(1);
		//		<!-- step size to increase searchRadius if no stops are found -->
		config.transitRouter().setExtensionRadius(200);
		//		<!-- maximum beeline distance between stops that agents could transfer to by walking -->
		config.transitRouter().setMaxBeelineWalkConnectionDistance(100);
		//		<!-- the radius in which stop locations are searched, given a start or target coordinate -->
		config.transitRouter().setSearchRadius(1000);



		Scenario scenario = ScenarioUtils.loadScenario(config);

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






		boolean subsidies = true;
		if (subsidies) {
			PConfigGroup pConfig = ConfigUtils.addOrGetModule(config, PConfigGroup.class);
			pConfig.setUseSubsidyApproach(true);
			pConfig.setSubsidyApproach("perPassenger");
			//pConfig.setSubsidyApproach(null);
			pConfig.setRouteProvider("TimeAwareComplexCircleScheduleProvider");
			pConfig.setGridSize(500); // manser used 3000
			pConfig.setPassengerCarEquivalents(1);
			pConfig.setNumberOfIterationsForProspecting(10);
			pConfig.setServiceAreaFile("");
			pConfig.setVehicleMaximumVelocity(16.6);
			pConfig.setRouteProvider("TimeAwareComplexCircleScheduleProvider");


		}


		controler.run();

	}

}
