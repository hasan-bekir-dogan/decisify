package com.decisify.decision.service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import com.decisify.decision.domain.Alternative;
import com.decisify.decision.domain.Criterion;
import com.decisify.decision.domain.Decision;
import com.decisify.decision.domain.Recommendation;
import com.decisify.decision.dto.AlternativeScoreResponse;
import com.decisify.decision.dto.DecisionCalculationResponse;
import com.decisify.decision.dto.DecisionCreateRequest;
import com.decisify.decision.dto.DecisionResponse;
import com.decisify.decision.dto.DecisionUpdateRequest;
import com.decisify.decision.dto.RankedAlternativeResponse;
import com.decisify.decision.dto.RecommendationResponse;
import com.decisify.decision.exception.DecisionNotFoundException;
import com.decisify.decision.exception.RecommendationNotFoundException;
import com.decisify.decision.repository.AlternativeRepository;
import com.decisify.decision.repository.CriterionRepository;
import com.decisify.decision.repository.DecisionRepository;
import com.decisify.decision.repository.RecommendationRepository;

@Service
public class DecisionServiceImpl implements DecisionService{

    private final DecisionRepository decisionRepository;
    private final AlternativeRepository alternativeRepository;
    private final CriterionRepository criterionRepository;
    private final NormalizationService normalizationService;
    private final ScoringService scoringService;
    private final RecommendationRepository recommendationRepository;

    public DecisionServiceImpl(
        DecisionRepository decisionRepository,
        AlternativeRepository alternativeRepository,
        CriterionRepository criterionRepository,
        NormalizationService normalizationService,
        ScoringService scoringService,
        RecommendationRepository recommendationRepository
    ) {
        this.decisionRepository = decisionRepository;
        this.alternativeRepository = alternativeRepository;
        this.criterionRepository = criterionRepository;
        this.normalizationService = normalizationService;
        this.scoringService = scoringService;
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public DecisionResponse create(DecisionCreateRequest request) {

        Decision decision = Decision.create(
            request.userId(), 
            request.title(), 
            request.description()
        );

        Decision savedDecision = decisionRepository.save(decision);

        return toResponse(savedDecision);
    }

    @Override
    public DecisionResponse update(UUID id, DecisionUpdateRequest decisionUpdateRequest) {
        Decision decision = getDecisionById(id);

        decision.update(
            decisionUpdateRequest.title(),
            decisionUpdateRequest.description()
        );

        return toResponse(decision);
    }

    @Override
    public DecisionResponse getById(UUID id) {
        Decision decision = getDecisionById(id);

        return toResponse(decision);
    }

    @Override
    public List<DecisionResponse> getAll() {
        return decisionRepository.findAll()
            .stream()
            .map(decision -> toResponse(decision))
            .toList();
    }

    @Override
    public void delete(UUID id) {
        Decision decision = getDecisionById(id);

        decisionRepository.delete(decision);
    }

    @Override
    public DecisionCalculationResponse calculate(UUID decisionId) {

        // Ensure the decision exists before starting the calculation.
        decisionRepository.findById(decisionId)
                .orElseThrow(() -> new DecisionNotFoundException(decisionId));

        // Normalize every criterion belonging to this decision.
        List<Criterion> criteria =
                criterionRepository.findByDecisionId(decisionId);

        for (Criterion criterion : criteria) {
            normalizationService.normalize(criterion.getId());
        }

        // Calculate scores for every alternative.
        List<Alternative> alternatives =
                alternativeRepository.findByDecisionId(decisionId);

        List<RankedAlternativeResponse> rankedAlternatives =
                alternatives.stream()
                        .map(alternative -> {
                            AlternativeScoreResponse score =
                                    scoringService.calculateScore(alternative.getId());

                            return new RankedAlternativeResponse(
                                    alternative.getId(),
                                    alternative.getName(),
                                    score.score(),
                                    0,
                                    score.contributions()
                            );
                        })
                        .sorted(
                                Comparator.comparing(
                                        RankedAlternativeResponse::score
                                ).reversed()
                        )
                        .toList();

        // Assign ranks after sorting.
        List<RankedAlternativeResponse> rankedWithPositions =
                IntStream.range(0, rankedAlternatives.size())
                        .mapToObj(index -> {
                            RankedAlternativeResponse alternative =
                                    rankedAlternatives.get(index);

                            return new RankedAlternativeResponse(
                                    alternative.alternativeId(),
                                    alternative.alternativeName(),
                                    alternative.score(),
                                    index + 1,
                                    alternative.contributions()
                            );
                        })
                        .toList();

        // Persist the highest-ranked alternative as the recommendation.
        if (!rankedWithPositions.isEmpty()) {
            RankedAlternativeResponse winner = rankedWithPositions.get(0);

            Recommendation recommendation = Recommendation.create(
                    decisionId,
                    winner.alternativeId(),
                    winner.score(),
                    "Selected as the highest-scoring alternative."
            );

            recommendationRepository.save(recommendation);
        }

        return new DecisionCalculationResponse(
                decisionId,
                rankedWithPositions
        );
    }

    @Override
    public RecommendationResponse getRecommendation(UUID decisionId) {

        // Ensure the decision exists.
        getDecisionById(decisionId);

        Recommendation recommendation = recommendationRepository
                .findTopByDecisionIdOrderByCreatedAtDesc(decisionId)
                .orElseThrow(() -> new RecommendationNotFoundException(decisionId));

        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getDecisionId(),
                recommendation.getSelectedAlternativeId(),
                recommendation.getFinalScore(),
                recommendation.getExplanation(),
                recommendation.getCreatedAt()
        );
    }

    private Decision getDecisionById(UUID id) {
        return decisionRepository.findById(id).orElseThrow(() -> 
            new DecisionNotFoundException(id)
        );
    }

    private DecisionResponse toResponse(Decision decision) {
        return new DecisionResponse(
            decision.getId(), 
            decision.getUserId(), 
            decision.getTitle(), 
            decision.getDescription(), 
            decision.getStatus(), 
            decision.getCreatedAt(), 
            decision.getUpdatedAt()
        );
    }
}
