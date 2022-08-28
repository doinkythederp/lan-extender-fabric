# Contributing to LAN Extender

Thanks for taking the time to help this mod improve! All contributions are
helpful and welcome.

## Table of Contents

- [Contributing to LAN Extender](#contributing-to-lan-extender)
  - [Table of Contents](#table-of-contents)
  - [I have a question](#i-have-a-question)
  - [Ways to contribute](#ways-to-contribute)
    - [Reporting a problem](#reporting-a-problem)
      - [If you find an issue that describes the same problem](#if-you-find-an-issue-that-describes-the-same-problem)
      - [Writing and submitting your report](#writing-and-submitting-your-report)
    - [Suggesting features](#suggesting-features)
      - [If you find an feature request that descibes your idea](#if-you-find-an-feature-request-that-descibes-your-idea)
      - [Writing and submitting your suggestion](#writing-and-submitting-your-suggestion)
    - [Contributing code](#contributing-code)
      - [Variable name styleguide](#variable-name-styleguide)
      - [Commit message styleguide](#commit-message-styleguide)
      - [Pull requests](#pull-requests)
  - [Acknowledgements](#acknowledgements)

## I have a question

If you just have a question about the mod or need help using it, the best way you can
get support is by creating a Help & Support post in the mod's [Discussions](https://github.com/doinkythederp/lan-extender-fabric/discussions/categories/help-and-support) tab.

## Ways to contribute

### Reporting a problem

If something is not working as expected, you can use the repository's
[Issues][issues-page] page to report it. Before
creating a bug report, use the search bar to make sure that what you're
experiencing isn't already a known issue.

#### If you find an issue that describes the same problem

If the issue you found is *closed*, it's fine to make a new one, but make sure
to link the one you found under the **Additional information** header.

However, if the issue you found is *open*, the best way to help is by leaving a
comment on it, describing your experience.

#### Writing and submitting your report

When creating your report, you should use the **Bug report** issue template to
be provided with a list of questions that will help describe the problem you are
having.

Here are some recomendations on how to create a good bug report request:

- Give the issue a **clear and concise** title.
- Fill out **as many of the issue template's headers as possible**.
- Provide instructions on how to cause the issue.
- Provide your LAN Extender version, Minecraft version, and operating system.
- Attach **screenshots or GIFs** to help display the problem.
- Explain **when the problem started happening**. Was it after a recent update?
  Or has it always been happening?

### Suggesting features

First of all, thanks for wanting to share your idea! Feature requests help this
project grow.

However, your idea may have already been discussed. Please use the search bar to
see if there are any similar suggestions!

#### If you find an feature request that descibes your idea

If the issue is *open,* send it a :+1: emoji! If it's closed, it's possible it
has already been implemented or is not planned.

#### Writing and submitting your suggestion

When creating your report, you should use the **Feature request** issue template
to be provided with a list of questions that will help describe the suggestion
you are submitting.

Here are some recomendations on how to create a good feature request:

- Give the issue a **clear and concise** title.
- Fill out **as many of the template's headers as possible**.
- Provide **screenshots, or GIFs** to help readers understand what
  you're saying.

### Contributing code

The simplest way to start contributing code to LAN Extender is by finding an
[Issue][issues-page] to tackle. Each one requests changes to the project, and some are
more involved than others. You should be familar with Git and Java before you start
contributing code.

When you're ready to start coding, **fork the project**, then use `git clone` or `gh clone`
to clone the repository.

#### Variable name styleguide

Classes, enums, and interfaces should use the
PascalCase naming style (e.g. `Path2D`, `MinecraftClient`, `RESTClient`). Variables
and methods should use the camelCase naming style (e.g. `client`,
`contactInfo`, `installNgrokIfNeeded`).

When writing camelCase names, acronyms should be uppercase *unless* they are the
first word in the name: `urlIsValid` and `contactURL` are both correct.

Also, constants should use the SCREAMING_SNAKE_CASE naming style (e.g. `NARRATED_TEXT`, `VERSION`).

#### Commit message styleguide

LAN Extender uses [Conventional Commits][conventional-commits-website]
to keep commit messages informational. Conventional commits have the
following form:

```txt
type(Scope, this is optional): description

[optional body]

[optional footers]
```

Here is an example of a conforming commit message:

```txt
docs(Contributing): add Acknowledgements section
```

When writing the commit description, it works best to use the present imperative
tense ("add ABC" instead of "added ABC" or "adds ABC"). It might help to imagine
that you are telling someone to do something ("go add ABC"): `feat: add ABC`

Here is a list of common commit types:

| Type | Description |
|------|------------|
| chore | Changes to workspace & configuration files |
| feat | New features |
| fix | Bug fixes |
| refactor | Changes to internal logic |
| revert | Reversion of a previous change |
| style | Changes to code style and formatting |
| test | Changes or additions to unit tests |
| docs | Changes to documentation files |

#### Pull requests

When you're ready for your changes to be merged into the project, head over to the [Pull
Requests][pr-page] page and create a new pull request. Include a description of
what changed, and [link to the Issue][link-to-issue-guide] that you fixed.

If you're not done with your changes but are looking for feedback, you can
[mark it as a draft][about-draft-prs].

Once your pull request has been merged, congrats! Your changes and username will be credited
in the next release's changelog.

## Acknowledgements

This CONTRIBUTING.md file contains excerpts from and was inspired in part by the
Atom editor's CONTRIBUTING.md. [Click here to go check it
out.][atom-contributing]

[issues-page]: https://github.com/doinkythederp/lan-extender-fabric/issues
[pr-page]: https://github.com/doinkythederp/lan-extender-fabric/pulls
[conventional-commits-website]: https://conventionalcommits.org
[link-to-issue-guide]:
    https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue
[about-draft-prs]:
    https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests#draft-pull-requests
[atom-contributing]: https://github.com/atom/atom/blob/master/CONTRIBUTING.md
