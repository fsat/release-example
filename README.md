# Simple Deployment Pipeline: Example

This project is meant to accompany the [Simple Deployment Pipeline](https://docs.google.com/presentation/d/1Lpi3C6kZGCcdKy7hJEpn96g0LJus-RPR6x4bpuW8JTM/edit?usp=sharing) talk given to the [Melbourne Scala User Group Meetup](https://www.meetup.com/en-AU/Melbourne-Scala-User-Group/events/249019509/).

This project provides an example the _Cut the release_ step portion of the deployment pipeline talk.

This project is a SBT multi-module project, with `example` module being published to Travis whenever the merge to `master` occurs.

The artifact of the project is a runnable fat jar hosted in [Bintray](https://bintray.com/fsat/universal/release-example).

To deploy, the artifact can simply be downloaded and run with `java -jar <path to artifact jar file>` command.

## Building the project locally

To build the project locally, you will need to have:

* SBT version 1.0.* or above
* JDK 8 and above

Once you clone the project, run the following command to run all the test.

```
sbt test
```

To package the `example` module as fat jar:

```
sbt example/assembly
```

To publish the `example` module to bintray:

```
sbt example/publish
```

## Trying out the deployment pipeline

In order to try the deployment pipeline your self, you will need to setup Travis and Bintray with your own credentials.

You won't be able to try the deployment pipeline for yourself unless you have write permissions to the repository. The easiest way to obtain the write permission is to fork the repository. This would give you full permission as you would be the owner of the cloned repository.

As the deployment pipeline uses Travis and Bintray, the effort for this example is mostly spent on ensuring Travis has the ability to push release commits to Github and uploading artifact to Bintray.

### Sign up to Github

Please register with [Github](https://github.com/). This will allow you to clone this repository.

### Sign up to Bintray

* Visit [Bintray](https://bintray.com)
* Click on the `Sign In` link
* Click on `Sign in with Github`

### Create Bintray repository

Click on the `Add New Repository` link.

Create the new repository with the following details.

* Select `Public` repository
* `Name` of the repository is `universal`
* `Type` of the repository is `Generic`

### Fork the project

Click on the `Fork` button on the project, and fork the project into your own Github account.

### Enable Travis integration

* Click on the `Settings` tab
* Click on the `Integrations & services` on the left hand menu
* Click on `Add service` button
* Select `Travis CI`
* Click on `Add Service` (it's ok leave the form blank)

### Install Travis CLI

Travis CLI will be required for encrypting the credentials as the repository will be exposed as public repository.

In MacOS this can be done with [Homebrew](https://brew.sh/).

```
brew install travis
```

For other OS, you need to install the Travis CLI gem following the [install documentation](https://github.com/travis-ci/travis.rb#installation).

### Swap the Github deployment key

When releasing using [sbt-release](https://github.com/sbt/sbt-release), new commits and new release tag will be pushed to the branch where the release is performed. Because of this, write access to the repository is required.

When performing automated push to Github, the write access can be granted using deployment key.

The deployment key are essentially are public + private key pair. The public key is registered with Github, while the private key is encrypted using Travis CLI and stored in the repo. This key is then decrypted by Travis during the build.

First, generate the key pair. Enter `y` when prompted whether to override, and enter empty passphrase. _Passphrase must be empty to allow Travis accessing the private key without any password_

```
ssh-keygen -f .travis/travis-gh
```

The command above will generate the key pair in the `.travis/` directory.

Next, we'll encrypt the private key using Travis CLI.

```
travis login --auto
travis encrypt-file .travis/travis-gh
```

You should now have updated public key `.travis/travis-gh.pub` and updated, _encrypted_ private key `.travis/travis-gh.enc`.

Commit and push these two files.

_Discard the private key `.travis/travis-gh` so no one else but Travis is able to access the private key_

Obtain the content of the public key `.travis/travis-gh.pub` and follow the Github documentation on how to [register the public key as deployment key](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys) for your project.

Reference:

* [Github: deployment key](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys)
* [travis login](https://github.com/travis-ci/travis.rb#login)
* [travis encrypt-file](https://github.com/travis-ci/travis.rb#encrypt-file)

### Modify Bintray credentials

First, we must obtain the Bintray username and API key.

Login to Bintray. Your bintray username will be displayed on the top right corner next to your avatar.

To obtain the API key, [edit your profile](https://bintray.com/profile/edit), and then select `API Key` from the left menu.

Next, we must replace the credentials in the `.travis.yml`.

Encrypt the Bintray username.

```
travis encrypt BINTRAY_USER=<your username>
```

Replace the first `env.global.secure` in the `.travis.yml` with the value generated by the command above.

Encrypt the Bintray API key.

```
travis encrypt BINTRAY_PASS=<api key>
```

Replace the second `env.global.secure` in the `.travis.yml` with the value generated by the command above.

The environment variable `BINTRAY_USER` and `BINTRAY_PASS` will be used by [sbt-bintray](https://github.com/sbt/sbt-bintray)to form the Bintray credentials to upload the artifact.

Commit and push the change to `.travis.yml`.

### Testing the deploy pipeline

First, open a PR to _your_ fork. Ensure Travis PR build is green before merging to `master`.

Merging to `master` should trigger a `master` branch build, which will invoke the `sbt-release`. A new version will be incremented, and a new artifact will be published to Bintray.

If you `git pull` from the `master` branch, you should see the new versions being set by `sbt-release`.

The new artifact will be uploaded to the new Bintray repository you created.

To run the code, download the latest published jar, and then execute the following command.

```
java -jar <jar file> --host 0.0.0.0 --port 5678
```

If you access `http://localhost:5678` you would see the reply from the application you've just started.
