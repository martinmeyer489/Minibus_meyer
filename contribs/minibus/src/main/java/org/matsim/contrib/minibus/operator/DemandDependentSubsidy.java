//package org.matsim.contrib.minibus.operator;
//
//import com.google.inject.Inject;
//import org.matsim.api.core.v01.Coord;
//import org.matsim.api.core.v01.Id;
//import org.matsim.api.core.v01.Scenario;
//import org.matsim.api.core.v01.events.Event;
//import org.matsim.api.core.v01.population.Activity;
//import org.matsim.api.core.v01.population.Person;
//import org.matsim.api.core.v01.population.PlanElement;
//import org.matsim.contrib.minibus.PConfigGroup;
//import org.matsim.core.network.NetworkUtils;
//import org.matsim.pt.transitSchedule.api.TransitStopFacility;
//
//import java.util.*;
//
//public class DemandDependentSubsidy implements SubsidyI{
//
//    @Inject
//    Scenario scenario;
//    public DemandDependentSubsidy(){
//
//    }
//
//
//
//    @Override
//    public double getSubsidy(Id<PPlan> id) {
//        return 0;
//    }
//
//    @Override
//    public void computeSubsidy() {
//        HashMap<Id<TransitStopFacility>, Double> actBasedSub = new HashMap<>();
//        if(this.pConfig.getUseSubsidyApproach()) {
//            HashMap<Coord, Integer> nbActivities = new HashMap<>();
//            HashMap<TransitStopFacility, List<Integer>> nbActivitiesAroundStop = new HashMap<>();
//            for (Person person : scenario.getPopulation().getPersons().values()) {
//                for (PlanElement pE : person.getSelectedPlan().getPlanElements()) {
//                    if (pE instanceof Activity) {
//                        Activity act = (Activity) pE;
//                        if (!act.getType().equals("pt interaction") && !act.getType().equals("outside")) {
//                            nbActivities.putIfAbsent(act.getCoord(), 0);
//                            nbActivities.put(act.getCoord(), nbActivities.get(act.getCoord()) + 1);
//                        }
//                    }
//                }
//            }
//            for (TransitStopFacility stop : this.pStopsOnly.getFacilities().values()) {
//                nbActivitiesAroundStop.putIfAbsent(stop, new ArrayList<>(Arrays.asList(0,0)));
//                for (Coord actCoord : nbActivities.keySet()) {
//                    if(NetworkUtils.getEuclideanDistance(actCoord, stop.getCoord()) < 500)  {
//                        int nbActs = nbActivities.get(actCoord);
//                        nbActivitiesAroundStop.get(stop).set(0, nbActivitiesAroundStop.get(stop).get(0) + nbActs);
//                    }
//                    if(NetworkUtils.getEuclideanDistance(actCoord, stop.getCoord()) < 3000)  {
//                        int nbActs = nbActivities.get(actCoord);
//                        nbActivitiesAroundStop.get(stop).set(1, nbActivitiesAroundStop.get(stop).get(1) + nbActs);
//                    }
//                }
//            }
//
//
//            int counter = 0;
//
//            for(TransitStopFacility stop: nbActivitiesAroundStop.keySet())	{
//                double activities = nbActivitiesAroundStop.get(stop).get(0)+ (0.1 * nbActivitiesAroundStop.get(stop).get(1));
//
//                double subsidies = 150 - ( 20 * Math.pow(2, (activities * 0.0021) ) );
//                if(subsidies > 0.0)	{
//                    counter++;
//                    actBasedSub.put(stop.getId(), subsidies);
//                }
//            }
////			log.info("number of subsidized stops: " + counter);
//
//        }
//    }
//
//
//
//
//
//
//
//
//}
