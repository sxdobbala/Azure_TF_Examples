# Development Guidelines

1. Where should Source Code reside?
    * Source Code should be located in **__src/com/optum/jenkins/pipeline/library__**
2. Jenkins Plugins?
    * Preference is to build Jenkins libs using direct CLI vs Jenkins Plugins. 
    * This preference in approach is aligned with the Jenkins community shift away from plugins. In addition this approach will provide a simple user experience for the community as plugin installation will be required.
    * As with any guidance use your best judgment when applying these to your lib and there may be situations that a plugin makes sense.
