<!-- (c) https://github.com/MontiCore/monticore -->

The CI is (as of 2024) partially run via GitHub actions and for private
projects,
on the RWTH GitLab.

## Workflow [gradle_mc.yml](../.github/workflows/gradle_mc.yml)

Uses Gradle to test & build the project.

## Trigger MontiVerse [trigger_montiverse.yml](../.github/workflows/trigger_montiverse.yml)

The MontiVerse is a collection of language projects
Publishes the release to GitHub packages (using `$GITHUB_REPOSITORY`) and
the se-nexus.
In addition, the tutorial tar.gz is packaged and added as an artifact (TODO).

## Deploy Snapshot [deploy_snapshot_mc.yml](../.github/workflows/deploy_snapshot_mc.yml)

Publishes the snapshot to GitHub packages (using `$GITHUB_REPOSITORY`) and
the se-nexus.

## Deploy Release [deploy_release_mc.yml](../.github/workflows/deploy_release_mc.yml)

Publishes the release to GitHub packages (using `$GITHUB_REPOSITORY`) and
the se-nexus.
In addition, the tutorial tar.gz is packaged and added as an artifact (TODO).

# Secrets

| Name                     | Description                                                                                                                     | Workflows                             |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------|
| SE_NEXUS_USER            |                                                                                                                                 | deploy_snapshot_mc, deploy_release_mc |
| SE_NEXUS_PASSWORD        |                                                                                                                                 | deploy_snapshot_mc, deploy_release_mc |
| DOWNSTREAM_PAT           | Personal access token to trigger downstream projects on GitHub (fine grained: content: write, actions: write)                   | deploy_snapshot_mc                    |
| DOWNSTREAM_GITLAB_PAT    | Personal access token to trigger downstream projects on GitLab (not required for now)                                           | deploy_snapshot_mc                    |
| GITLAB_TOKEN             | Checks out the monticore-pygments-highlighting project from GitLab                                                              | prepare_pages                         |
| MONTIVERSE_TRIGGER_TOKEN | Triggers the MontiVerse IT-Pipeline on GitLab [more](https://github.com/digital-blueprint/gitlab-pipeline-trigger-action)       | gradle_mc                             |
| MONTIVERSE_ACCESS_TOKEN  | Reads the MontiVerse IT-Pipeline status from GitLab [more](https://github.com/digital-blueprint/gitlab-pipeline-trigger-action) | gradle_mc                             |


