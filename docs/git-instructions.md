# Git Instructions

## Git Commit Message Best Practices

Follow the Conventional Commits specification for clear, consistent commit messages:

### Commit Message Format

```plain
<type>(<scope>): <subject>

<body>

<footer>
```

### Commit Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Code style changes (formatting, missing semi-colons, etc.)
- **refactor**: Code refactoring (neither bug fix nor feature)
- **perf**: Performance improvements
- **test**: Adding or modifying tests
- **chore**: Maintenance tasks, build process, auxiliary tools, libraries

### Subject Line Rules

- Use imperative mood: "Add feature" not "Added feature"
- Keep under 50 characters
- Capitalize first letter
- No period at the end
- Include scope when applicable: `feat(controller): Add organization endpoint`

### Body Guidelines

- Wrap at 72 characters
- Explain what and why, not how
- Use bullet points for multiple changes
- Include motivation and context

### Breaking Changes

- Add `BREAKING CHANGE:` footer with description
- Use `feat` type with `!` after type/scope: `feat(api)!: Remove deprecated endpoint`

### Examples

**Good:**

```plain
feat(service): Add organization creation functionality

- Implement createOrganization method with validation
- Add business logic for duplicate name checking
- Integrate with database repository layer

Closes #123
```

**Bad:**

```plain
Added organization creation
fixed some bugs
updated dependencies
```

### Footer Usage

- Reference issues: `Closes #123`, `Fixes #456`
- Add breaking change notices
- Include co-authorship for collaborative work
- Add sign-off when required

### Commit Message Workflow

1. Stage changes with `git add`
2. Create commit with clear message
3. Verify with `git log --oneline -5`
4. Push to remote when ready

🤖 Generated commits should include attribution line:

```plain
🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```
