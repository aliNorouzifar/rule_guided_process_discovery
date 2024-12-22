package minerful.interestingness.test.constraint;

import java.util.Set;
import java.util.TreeSet;

import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import minerful.automaton.concept.relevance.RelevanceAwareTransition;
import minerful.automaton.concept.relevance.TransitionRelevance;
import minerful.automaton.concept.relevance.VacuityAwareWildcardAutomaton;
import minerful.concept.TaskChar;
import minerful.concept.TaskCharSet;
import minerful.concept.constraint.Constraint;
import minerful.concept.constraint.ConstraintFamily;
import minerful.concept.constraint.ConstraintFamily.ConstraintSubFamily;
import minerful.concept.constraint.relation.Response;
import minerful.io.encdec.TaskCharEncoderDecoder;

public class SequenceResponse21 extends Constraint {
	public SequenceResponse21(TaskChar param1, TaskChar param2, TaskChar param3) {
		super();
		this.parameters.add(new TaskCharSet(param1));
		this.parameters.add(new TaskCharSet(param2));
		this.parameters.add(new TaskCharSet(param3));
	}

	@Override
	public String getRegularExpressionTemplate() {
		return "([^%1$s]*)|([^%1$s]*%1$s[^%2$s]*)|([^%1$s]*%1$s[^%2$s]*%2$s[^%3$s]*%3$s[^%1$s]*)";
				// "([^A]*)|([^A]*A[^B]*)|([^A]*A[^B]*B[^X]*X[^A]*)";
	}

	@Override
	public String getRegularExpression() {
		return String.format(this.getRegularExpressionTemplate(),
				this.parameters.get(0).toPatternString(),
				this.parameters.get(1).toPatternString(),
				this.parameters.get(2).toPatternString()
		);
	}

	@Override
	public String getNegativeRegularExpression() {
		return String.format(this.getNegativeRegularExpressionTemplate(),
				this.parameters.get(0).toPatternString(),
				this.parameters.get(1).toPatternString(),
				this.parameters.get(2).toPatternString()
		);
	}


	@Override
	public TaskCharSet getImplied() {
		return null;
	}

	@Override
	public Constraint suggestConstraintWhichThisShouldBeBasedUpon() {
		return null;
	}

	@Override
	public Constraint copy(TaskChar... taskChars) {
		this.checkParams(taskChars);
		return new SequenceResponse21(taskChars[0], taskChars[1], taskChars[2]);
	}

	@Override
	public Constraint copy(TaskCharSet... taskCharSets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkParams(TaskChar... taskChars)
			throws IllegalArgumentException {
		return true;
	}

	@Override
	public boolean checkParams(TaskCharSet... taskCharSets)
			throws IllegalArgumentException {
		return true;
	}

	@Override
	public ConstraintFamily getFamily() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends ConstraintSubFamily> T getSubFamily() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Constraint t) {
        int result = super.compareTo(t);
        if (result == 0) {
            result = this.getClass().getCanonicalName().compareTo(t.getClass().getCanonicalName());
        }
        return result;
	}

	@Override
	public boolean isBranched() {
		return false;
	}

	@Override
	public VacuityAwareWildcardAutomaton getCheckAutomaton() {
		VacuityAwareWildcardAutomaton autom = new VacuityAwareWildcardAutomaton(this.toString(),
				this.getRegularExpression(), TaskCharEncoderDecoder.getTranslationMap(this.getInvolvedTaskChars()));
		
		// TODO Tweaking to insert the loopback to the initial state
		State state = autom.getInitialState();
		int i = 0;
		for (; i < this.getParameters().size() - 1; i++) {
			state = state.step(this.parameters.get(i).getFirstTaskChar().identifier);
		}
		State stateToRemove = state.step(this.parameters.get(i).getFirstTaskChar().identifier);
		state.getTransitions().remove(new Transition(this.parameters.get(i).getFirstTaskChar().identifier, stateToRemove));
		RelevanceAwareTransition newTransition = new RelevanceAwareTransition(this.parameters.get(i).getFirstTaskChar().identifier, autom.getInitialState(), this.parameters.get(i).getFirstTaskChar().taskClass.toString());
		newTransition.setRelevance(TransitionRelevance.RELEVANT);
		state.addTransition(newTransition);
		
		return autom;
	}

	@Override
	public String getLTLpfExpressionTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getNegativeLTLpfExpressionTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getNegativeRegularExpressionTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Constraint getSymbolic() {
		return new SequenceResponse21(TaskChar.SYMBOLIC_TASKCHARS[0], TaskChar.SYMBOLIC_TASKCHARS[1], TaskChar.SYMBOLIC_TASKCHARS[2]);
	}
}
