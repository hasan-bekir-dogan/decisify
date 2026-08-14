CREATE TABLE decisions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE alternatives (
    id UUID PRIMARY KEY,
    decision_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    CONSTRAINT fk_alternatives_decision
        FOREIGN KEY (decision_id)
        REFERENCES decisions(id)
        ON DELETE CASCADE
);

CREATE TABLE criteria (
    id UUID PRIMARY KEY,
    decision_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    weight REAL NOT NULL,
    unit VARCHAR(100) NOT NULL,
    higher_is_better BOOLEAN NOT NULL,

    CONSTRAINT fk_criteria_decision
        FOREIGN KEY (decision_id)
        REFERENCES decisions(id)
        ON DELETE CASCADE
);

CREATE TABLE criterion_values (
    id UUID PRIMARY KEY,
    alternative_id UUID NOT NULL,
    criterion_id UUID NOT NULL,
    raw_value REAL,
    normalized_value REAL,
    source VARCHAR(100),
    confidence REAL,

    CONSTRAINT fk_criterion_values_alternative
        FOREIGN KEY (alternative_id)
        REFERENCES alternatives(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_criterion_values_criterion
        FOREIGN KEY (criterion_id)
        REFERENCES criteria(id)
        ON DELETE CASCADE,
    
    CONSTRAINT uq_criterion_values_alternative_criterion
        UNIQUE (alternative_id, criterion_id)
);

CREATE TABLE recommendations (
    id UUID PRIMARY KEY,
    decision_id UUID NOT NULL,
    selected_alternative_id UUID NOT NULL,
    final_score REAL NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_recommendations_decision
        FOREIGN KEY (decision_id)
        REFERENCES decisions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recommendations_selected_alternative
        FOREIGN KEY (selected_alternative_id)
        REFERENCES alternatives(id)
);

