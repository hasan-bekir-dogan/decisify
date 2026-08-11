# Git Workflow

This document defines the Git workflow and contribution conventions used in the Decisify project.

## Branch Naming

Branches are created from `main` and should follow this format:

`<type>/<issue-number>-<short-description>`

Supported branch types:

- `feature/` — New functionality
- `fix/` — Bug fixes
- `chore/` — Maintenance, configuration, or infrastructure work
- `docs/` — Documentation changes
- `refactor/` — Internal code improvements without changing behavior
- `test/` — Test additions or improvements
- `ci/` — CI/CD workflow changes

Examples:

```text
feature/12-user-registration
fix/27-document-upload-validation
chore/3-environment-configuration
docs/4-git-workflow
ci/18-decision-service-pipeline
```

Before creating a new branch:

```bash
git checkout main
git pull origin main
git checkout -b docs/4-git-workflow
```

## Commit Messages

Commit messages follow a Conventional Commits-style format:

`<type>: <short description>`

Common types:

- `feat:` — New functionality
- `fix:` — Bug fix
- `docs:` — Documentation
- `chore:` — Maintenance or configuration
- `refactor:` — Code restructuring without behavioral changes
- `test:` — Tests
- `ci:` — CI/CD changes
- `build:` — Build system or dependency changes

Examples:

```text
feat: add decision creation endpoint
fix: validate duplicate user email
docs: document git workflow
chore: add environment configuration
test: add decision service unit tests
ci: add auth service build workflow
```

Each commit should represent one logical change.

## Pull Requests

All changes to `main` should be introduced through pull requests.

Each pull request should:

- Reference the related issue.
- Describe the implemented changes.
- Stay focused on a single issue or concern.
- Avoid unrelated changes.
- Pass all required CI checks before merge.

When a pull request completes an issue, include:

```text
Closes #<issue-number>
```

Example:

```text
Closes #12
```

## CI Before Merge

Changes must not be merged into `main` while required CI checks are failing.

Expected workflow:

```text
Issue
  ↓
Branch
  ↓
Development
  ↓
Commit(s)
  ↓
Pull Request
  ↓
CI Checks
  ↓
Merge to main
```

A pull request is ready to merge when:

1. The implementation satisfies the issue requirements.
2. Required automated checks pass.
3. No secrets or generated artifacts are included.
4. The branch contains no unintended changes.

Branch protection should require CI checks before merge once the relevant CI workflows are available.

## Merge Strategy

Prefer **Squash and Merge** for short-lived branches.

The final squashed commit should follow the same commit-message convention.

Example:

```text
feat: implement user registration
```
