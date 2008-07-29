package playground.jhackney.algorithms;

import org.matsim.plans.Person;
import org.matsim.plans.algorithms.PersonAlgorithm;
import org.matsim.socialnetworks.algorithms.PersonCalculateActivitySpaces;

import edu.uci.ics.jung.statistics.StatisticalMoments;

public class PersonCalcASD2  extends PersonAlgorithm{
	PersonCalculateActivitySpaces pcasd1 = new PersonCalculateActivitySpaces();
	public StatisticalMoments smASD2 = new StatisticalMoments();

	private void addVal(double val){
		if (val != val) {
			// val is NaN
		}
		else {
			smASD2.accumulate(val);
		}
	}
	
	@Override
	public void run(Person person) {
		// TODO Auto-generated method stub
		double aSd2 = pcasd1.getPersonASD2(person.getSelectedPlan());
		System.out.println("#Result "+aSd2);
		addVal(aSd2);

	}

}
