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
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.minibus.PConfigGroup;
import org.matsim.contrib.minibus.hook.PModule;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.population.algorithms.TripsToLegsAlgorithm;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.PtConstants;
import org.opengis.referencing.FactoryException;

import java.util.*;

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

        //config.global().setNumberOfThreads(32);
        //config.parallelEventHandling().setNumberOfThreads(1);
        //config.qsim().setNumberOfThreads(32);

        config.controler().setLastIteration(600);
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

        //Adding randomness to the router, sigma = 3
        //config.plansCalcRoute().setRoutingRandomness(3);


        setNetworkModeRouting(controler);





        // mapping agents' activities to links on the road network to avoid being stuck on the transit network
        mapActivities2properLinks(scenario);



        controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                this.bind(PrepareForSimImpl.class);
                this.bind(PrepareForSim.class).to(SantiagoPrepareForSim.class);
            }
        });

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
    private static void mapActivities2properLinks(Scenario scenario) {
        Network subNetwork = getNetworkWithProperLinksOnly(scenario.getNetwork());
        for(Person person : scenario.getPopulation().getPersons().values()){
            for (Plan plan : person.getPlans()) {
                for (PlanElement planElement : plan.getPlanElements()) {
                    if (planElement instanceof Activity) {
                        Activity act = (Activity) planElement;
                        Id<Link> linkId = act.getLinkId();
                        if(!(linkId == null)){
                            throw new RuntimeException("Link Id " + linkId + " already defined for this activity. Aborting... ");
                        } else {
                            linkId = NetworkUtils.getNearestLink(subNetwork, act.getCoord()).getId();
                            act.setLinkId(linkId);
                        }
                    }
                }
            }
        }
    }

    private static Network getNetworkWithProperLinksOnly(Network network) {
        Network subNetwork;
        TransportModeNetworkFilter filter = new TransportModeNetworkFilter(network);
        Set<String> modes = new HashSet<String>();
        modes.add(TransportMode.car);
        subNetwork = NetworkUtils.createNetwork();
        filter.filter(subNetwork, modes); //remove non-car links

        for(Node n: new HashSet<Node>(subNetwork.getNodes().values())){
            for(Link l: NetworkUtils.getIncidentLinks(n).values()){
                if(l.getFreespeed() > (16.666666667)){
                    subNetwork.removeLink(l.getId()); //remove links with freespeed > 60kmh
                }
            }
            if(n.getInLinks().size() == 0 && n.getOutLinks().size() == 0){
                subNetwork.removeNode(n.getId()); //remove nodes without connection to links
            }
        }
        return subNetwork;
    }

    private static void setNetworkModeRouting(Controler controler) {
        controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                addTravelTimeBinding(TransportMode.ride).to(networkTravelTime());
                addTravelDisutilityFactoryBinding(TransportMode.ride).to(carTravelDisutilityFactoryKey());

                addTravelTimeBinding(SantiagoScenarioConstants.Modes.taxi.toString()).to(networkTravelTime());
                addTravelDisutilityFactoryBinding(SantiagoScenarioConstants.Modes.taxi.toString()).to(carTravelDisutilityFactoryKey());

                addTravelTimeBinding(SantiagoScenarioConstants.Modes.colectivo.toString()).to(networkTravelTime());
                addTravelDisutilityFactoryBinding(SantiagoScenarioConstants.Modes.colectivo.toString()).to(carTravelDisutilityFactoryKey());

                addTravelTimeBinding(SantiagoScenarioConstants.Modes.other.toString()).to(networkTravelTime());
                addTravelDisutilityFactoryBinding(SantiagoScenarioConstants.Modes.other.toString()).to(carTravelDisutilityFactoryKey());
            }
        });
    }
}


