/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minerful.miner;

import java.util.Arrays;
import java.util.Set;

import minerful.concept.TaskChar;
import minerful.concept.TaskCharArchive;
import minerful.concept.constraint.Constraint;
import minerful.concept.constraint.MetaConstraintUtils;
import minerful.concept.constraint.ConstraintsBag;
import minerful.miner.stats.GlobalStatsTable;
import minerful.miner.stats.LocalStatsWrapper;

public abstract class ExistenceConstraintsMiner extends AbstractConstraintsMiner {
    public ExistenceConstraintsMiner(GlobalStatsTable globalStats, TaskCharArchive taskCharArchive, Set<TaskChar> tasksToQueryFor) {
		super(globalStats, taskCharArchive, tasksToQueryFor);
	}
    
    @Override
    public ConstraintsBag discoverConstraints(ConstraintsBag constraintsBag) {
        if (constraintsBag == null)
            constraintsBag = new ConstraintsBag(tasksToQueryFor);
        for (TaskChar task: tasksToQueryFor) {
            LocalStatsWrapper localStats = this.globalStats.statsTable.get(task);
            TaskChar base = task;

            Constraint[] uniqueness = this.discoverMaxMultiplicityConstraints(base, localStats, this.globalStats.logSize, this.globalStats.numOfEvents);
            if (uniqueness != null)
            	constraintsBag.addAll(base, Arrays.asList(uniqueness));
            Constraint[] participation = this.discoverMinMultiplicityConstraints(base, localStats, this.globalStats.logSize, this.globalStats.numOfEvents);
            if (participation != null)
            	constraintsBag.addAll(base, Arrays.asList(participation));
            
            Constraint init = this.discoverEndConstraint(base, localStats, this.globalStats.logSize, this.globalStats.numOfEvents);
            if (init != null)
                constraintsBag.add(base, init);
            Constraint end = this.discoverInitConstraint(base, localStats, this.globalStats.logSize, this.globalStats.numOfEvents);
            if (end != null)
                constraintsBag.add(base, end);
        }
        return constraintsBag;
    }

	@Override
	public long howManyPossibleConstraints() {
		return MetaConstraintUtils.NUMBER_OF_DISCOVERABLE_EXISTENCE_CONSTRAINT_TEMPLATES * tasksToQueryFor.size();
	}

	protected abstract Constraint[] discoverMinMultiplicityConstraints(TaskChar base,
			LocalStatsWrapper localStats, long testbedSize, long numOfEventsInLog);

	protected abstract Constraint[] discoverMaxMultiplicityConstraints(TaskChar base,
			LocalStatsWrapper localStats, long testbedSize, long numOfEventsInLog);

	protected abstract Constraint discoverInitConstraint(TaskChar base,
			LocalStatsWrapper localStats, long testbedSize, long numOfEventsInLog);

	protected abstract Constraint discoverEndConstraint(TaskChar base,
			LocalStatsWrapper localStats, long testbedSize, long numOfEventsInLog);
}