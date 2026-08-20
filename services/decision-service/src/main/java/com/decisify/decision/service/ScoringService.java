package com.decisify.decision.service;

import com.decisify.decision.dto.AlternativeScoreResponse;

import java.util.UUID;

public interface ScoringService {

    AlternativeScoreResponse calculateScore(UUID alternativeId);
}