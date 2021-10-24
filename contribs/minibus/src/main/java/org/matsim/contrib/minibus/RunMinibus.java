

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
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
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

		Config config = ConfigUtils.loadConfig( "C:/Users/marti/Documents/MA/input/v5.5/v5.5_1pct_only_test/berlin-v5.5.3-1pct.output_config_p_module.xml", new PConfigGroup() ) ;
//		Config config = ConfigUtils.loadConfig("/Users/MeyerMa/IdeaProjects/minibus_meyer/Input/config.xml", new PConfigGroup() ) ;
		config.network().setInputFile("C:/Users/marti/Documents/MA/input/current standard input/berlin-v5.5.3-1pct.output_network.xml.gz");
		config.global().setCoordinateSystem("EPSG:31468");
		config.global().setRandomSeed(2);
		config.global().setNumberOfThreads(8);

		//config.plans().setInputFile("C:/Users/marti/Documents/MA/input/v5.4/v5.4_10pct/berlin-v5.4-10pct.plans_act_inside_prep.xml.gz");
		//config.plans().setInputFile("C:/Users/marti/Documents/MA/input/current standard input/berlin-v5.4-1pct.plans_activity_inside_prep2.xml");
		config.plans().setInputFile("C:/Users/marti/Documents/MA/input/v5.5/v5.5_1pct_only_test/berlin-v5.5-1pct.plans_act_inside_prep.xml.gz");

		//config.plans().setRemovingUnneccessaryPlanAttributes(true);
		config.plans().setHandlingOfPlansWithoutRoutingMode(useMainModeIdentifier);
		//config.plans().setNetworkRouteType("LinkNetworkRoute");

		MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();

		TripsToLegsAlgorithm algorithm = new TripsToLegsAlgorithm(mainModeIdentifier);










		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory("C:/Users/marti/Documents/MA/output_1pct_vehicletypes_actbased_30_5_config_tirachini_allcarlinks");
		config.controler().setRunId("per_passenger_1");
		config.controler().setLastIteration(400);
		config.controler().setWriteEventsInterval(400);
		config.controler().setWritePlansInterval(400);

		//config.controler().setWriteSnapshotsInterval(1);
		//config.controler().setRoutingAlgorithmType(ControlerConfigGroup.RoutingAlgorithmType.valueOf("Dijkstra")); //manser used Dijlstra and i use a star landmarks
		//config.controler().setCompressionType(ControlerConfigGroup.CompressionType.valueOf("none"));
//
//		config.planCalcScore().setBrainExpBeta(1);
//		config.planCalcScore().setPathSizeLogitBeta(1);
//		config.planCalcScore().setFractionOfIterationsToStartScoreMSA(null);
//		config.planCalcScore().setLearningRate(1);
//		config.planCalcScore().setUsingOldScoringBelowZeroUtilityDuration(false);
//		config.planCalcScore().setWriteExperiencedPlans(false);


		//config.plansCalcRoute().setNetworkModes(Collections.singleton("car"));
		//config.plansCalcRoute().setRoutingRandomness(3);


		//config.qsim().setEndTime(Double.parseDouble("30:00:00"));
		//config.qsim().setFlowCapFactor(0.1);
		//config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(false);
		//config.qsim().isRestrictingSeepage(true);
		//config.qsim().isSeepModeStorageFree(false);
		//config.qsim().setLinkDynamics(QSimConfigGroup.LinkDynamics.valueOf("FIFO"));
		//config.qsim().setLinkWidthForVis(30.0F);
		//config.qsim().setMainModes(Collections.singleton("car"));
		//config.qsim().setNodeOffset(0);
		//config.qsim().setNumberOfThreads(8);
		//config.qsim().setRemoveStuckVehicles(false);
		//config.qsim().setSeepModes(Collections.singleton("bike"));
		//config.qsim().setSimEndtimeInterpretation(QSimConfigGroup.EndtimeInterpretation.valueOf("onlyUseEndtime"));
		//config.qsim().setSimStarttimeInterpretation(QSimConfigGroup.StarttimeInterpretation.valueOf("maxOfStarttimeAndEarliestActivityEnd"));
		//config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.valueOf("equiDist"));
		//config.qsim().setSnapshotPeriod(Double.parseDouble("00:00:00"));
		//config.qsim().setStartTime("undefined");
		//config.qsim().setStorageCapFactor(1.0);
		//config.qsim().setStuckTime(108000.0);
		//config.qsim().setTimeStepSize(Double.parseDouble("00:00:01"));
		//config.qsim().setTrafficDynamics(QSimConfigGroup.TrafficDynamics.valueOf("kinematicWaves"));
		//config.qsim().setUseLanes(false);


//		config.strategy().setExternalExeConfigTemplate(null);
//		config.strategy().setExternalExeTimeOut(3600);
//		config.strategy().setMaxAgentPlanMemorySize(1);
//		config.strategy().setPlanSelectorForRemoval("WorstPlanSelector");

		// hier noch unterschiede zu manser


		//<!-- Defines the chain-based modes, seperated by commas -->
		//config.subtourModeChoice().setChainBasedModes(new String[]{"car,bike"});
		//<!-- Defines whether car availability must be considered or not. A agent has no car only if it has no license, or never access to a car -->
		//config.subtourModeChoice().setConsiderCarAvailability(false);
		//<!-- Defines all the modes available, including chain-based modes, seperated by commas -->
		//config.subtourModeChoice().setModes(new String[]{"car,pt,bike,walk"});






		//config.transit().setTransitModes(Collections.singleton("pt"));
		//<!-- Set this parameter to true if transit should be simulated, false if not. -->
		config.transit().setUseTransit(true);
		config.transit().setTransitScheduleFile("C:/Users/marti/Documents/MA/input/current standard input/berlin-v5.5.3-1pct.output_transitSchedule_no_bus_in_spandau.xml.gz");
		config.transit().setVehiclesFile("C:/Users/marti/Documents/MA/input/current standard input/berlin-v5.5.3-1pct.output_transitVehicles.xml.gz");



//		<!-- additional time the router allocates when a line switch happens. Can be interpreted as a 'safety' time that agents need to safely transfer from one line to another -->
		//config.transitRouter().setAdditionalTransferTime(0);
		//		<!-- Factor with which direct walk generalized cost is multiplied before it is compared to the pt generalized cost.  Set to a very high value to reduce direct walk results. -->
		//config.transitRouter().setDirectWalkFactor(1);
		//		<!-- step size to increase searchRadius if no stops are found -->
		//config.transitRouter().setExtensionRadius(200);
		//		<!-- maximum beeline distance between stops that agents could transfer to by walking -->
		//config.transitRouter().setMaxBeelineWalkConnectionDistance(100);
		//		<!-- the radius in which stop locations are searched, given a start or target coordinate -->
		//config.transitRouter().setSearchRadius(1000);


//		config.travelTimeCalculator().setAnalyzedModes(Collections.singleton("car"));
//		config.travelTimeCalculator().setCalculateLinkToLinkTravelTimes(false);
//		config.travelTimeCalculator().setCalculateLinkTravelTimes(true);
//		config.travelTimeCalculator().setFilterModes(false);
//		config.travelTimeCalculator().setMaxTime(10800);
//		config.travelTimeCalculator().setSeparateModes(false);
		//<!-- How to deal with congested time bins that have no link entry events. `optimistic' assumes free speed (too optimistic); 'experimental_LastMile' is experimental and probably too pessimistic. -->
		//config.travelTimeCalculator().setTravelTimeAggregatorType("optimistic");
		//<!-- The size of the time bin (in sec) into which the link travel times are aggregated for the router -->
		//config.travelTimeCalculator().setTraveltimeBinSize(900);
		//<!-- possible values: nullTravelTimeCalculatorArray TravelTimeCalculatorHashMap -->
		//config.travelTimeCalculator().setTravelTimeCalculatorType("TravelTimeCalculatorArray");
		//<!-- How to deal with link entry times at different positions during the time bin. Currently supported: average, linearinterpolation -->
		//config.travelTimeCalculator().setTravelTimeGetterType("average");


		Scenario scenario = ScenarioUtils.loadScenario(config);

		for (Person person: scenario.getPopulation().getPersons().values())	{
			Plan plan = person.getSelectedPlan();
			algorithm.run(plan);
//			for (PlanElement element : plan.getPlanElements()) {
//				if (element instanceof Activity) {
//					Activity activity = (Activity) element;
//					if (!Collections.singleton(PtConstants.TRANSIT_ACTIVITY_TYPE).contains(activity.getType())) {
//						activity.setType("h");
//					}
//				}
//			}
		}

		Controler controler = new Controler(scenario);

		controler.addOverridingModule(new PModule());






//		boolean subsidies = true;
//		if (subsidies) {
//			PConfigGroup pConfig = ConfigUtils.addOrGetModule(config, PConfigGroup.class);
//			pConfig.setUseSubsidyApproach(true);
//			pConfig.setSubsidyApproach("actBased");
//			//pConfig.setGridSize(250); // manser used 300
//			//pConfig.setPassengerCarEquivalents(0.3);
//			//pConfig.setNumberOfIterationsForProspecting(10);
//			pConfig.setServiceAreaFile("");
//			pConfig.setVehicleMaximumVelocity(16.6);
//			pConfig.setRouteProvider("TimeAwareComplexCircleScheduleProvider");
//
//		}


		controler.run();

	}

}
