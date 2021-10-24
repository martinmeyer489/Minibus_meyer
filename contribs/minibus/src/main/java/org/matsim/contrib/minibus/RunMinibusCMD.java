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
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.population.algorithms.TripsToLegsAlgorithm;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.PtConstants;
import org.opengis.referencing.FactoryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.matsim.core.config.groups.PlansConfigGroup.HandlingOfPlansWithoutRoutingMode.useMainModeIdentifier;


/**
 * Entry point, registers all necessary hooks
 *
 * @author aneumann
 */
public final class RunMinibusCMD {


    private final static Logger log = Logger.getLogger(RunMinibus.class);



    public static void main(final String[] args) throws CommandLine.ConfigurationException, FactoryException {
        List<String> cmdOptions = Arrays.asList("config-path", "iterations", "output-path", "demand-path",
                "network-path", "seed", "use-sub","sub-approach");
        List<String> mergedOptions = new ArrayList<String>();
        mergedOptions.addAll(cmdOptions);
        // Create command line object
        CommandLine cmd = new CommandLine.Builder(args)
                .allowOptions(mergedOptions.toArray(new String[mergedOptions.size()])).build();

        String configpath="/Users/MeyerMa/Desktop/MA/scenarios/berlin/output/van_automated_plans_all_modes_iter_400/van_automated_plans_all_modes_iter_400_seed_1.output_config.xml";
        if (cmd.hasOption("config-path")) {
            configpath = cmd.getOption("config-path").get();
        }
        Config config = ConfigUtils.loadConfig( configpath, new PConfigGroup() ) ;

        String networkpath="/Users/MeyerMa/IdeaProjects/data-science-matsim/jobs-infra/docker-build/input/minibus/berlin-v5.5.3-1pct.output_network.xml.gz";
        if (cmd.hasOption("network-path")) {
            networkpath = cmd.getOption("network-path").get();
        }
        config.network().setInputFile(networkpath);

        config.global().setCoordinateSystem("EPSG:31468");
        //config.global().setNumberOfThreads(32);
        //config.parallelEventHandling().setNumberOfThreads(1);
        //config.qsim().setNumberOfThreads(32);

        config.controler().setLastIteration(400);
        if (cmd.hasOption("iterations")) {
            config.controler().setLastIteration(Integer.parseInt(cmd.getOption("iterations").get()));
        }

        if (cmd.hasOption("seed")) {
            config.global().setRandomSeed(Integer.parseInt(cmd.getOption("seed").get()));
        }


        String demandpath="berlin-v5.4-1pct.plans_activity_inside_prep.xml";
        if (cmd.hasOption("demand-path")) {
            demandpath=cmd.getOption("demand-path").get();
        }
        config.plans().setInputFile(demandpath);

        config.plans().setRemovingUnneccessaryPlanAttributes(true);
        config.transit().setTransitScheduleFile("berlin-v5.5.3-1pct.output_transitSchedule_no_bus_in_spandau.xml.gz");
        config.transit().setVehiclesFile("berlin-v5.5.3-1pct.output_transitVehicles.xml.gz");


        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setOutputDirectory("/Users/MeyerMa/Desktop/MA/scenarios/berlin/output/test");
        if (cmd.hasOption("output-path")) {
            config.controler().setOutputDirectory(cmd.getOption("output-path").get());
        }



        config.plans().setHandlingOfPlansWithoutRoutingMode(useMainModeIdentifier);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        config.controler().setRunId("id");




        MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();
        TripsToLegsAlgorithm algorithm = new TripsToLegsAlgorithm(mainModeIdentifier);



        for (Person person: scenario.getPopulation().getPersons().values())	{
            Plan plan = person.getSelectedPlan();
            algorithm.run(plan);
//            for (PlanElement element : plan.getPlanElements()) {
//                if (element instanceof Activity) {
//                    Activity activity = (Activity) element;
//                    if (!Collections.singleton(PtConstants.TRANSIT_ACTIVITY_TYPE).contains(activity.getType())) {
//                        activity.setType("h");
//                    }
//                }
//            }
        }



        Controler controler = new Controler(scenario);

        controler.addOverridingModule(new PModule());


        if (Boolean.parseBoolean(cmd.getOption("use-sub").get())) {            PConfigGroup pConfig = ConfigUtils.addOrGetModule(config, PConfigGroup.class);
            pConfig.setUseSubsidyApproach(true);
            pConfig.setSubsidyApproach(cmd.getOption("sub-approach").get().toString());
            //pConfig.setGridSize(500); // manser used 300
            //pConfig.setPassengerCarEquivalents(1);
            //pConfig.setNumberOfIterationsForProspecting(10);
            //pConfig.setServiceAreaFile("");
            //pConfig.setVehicleMaximumVelocity(16.6);
            //pConfig.setRouteProvider("TimeAwareComplexCircleScheduleProvider");
        }



        controler.run();
    }

}


