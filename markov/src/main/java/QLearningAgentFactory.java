import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.QFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class QLearningAgentFactory implements LearningAgentFactory {
    @NonNull
    private String policyName;
    @NonNull
    private OOSADomain domain;
    @NonNull
    private Double gamma;
    @NonNull
    private HashableStateFactory hashingFactory;
    @NonNull
    private Double learningRate;
    @NonNull
    private Policy learningPolicy;
    @NonNull
    private Integer maxEpisodeSize;
    @NonNull
    private QFunction qInit;

    @Override
    public String getAgentName() {
        return String.format("%s Q-Learning", policyName);
    }

    @Override
    public LearningAgent generateAgent() {
        return new QLearning(
                domain,
                gamma,
                hashingFactory,
                qInit,
                learningRate,
                learningPolicy,
                maxEpisodeSize
        );
    }
}
