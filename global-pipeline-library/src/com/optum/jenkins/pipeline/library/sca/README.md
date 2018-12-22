# Fortify Scanning

## General Configurations

```groovy
// will attempt to scan all JS files in the dist directory and also generate a developer workbook report. 

 stage('Fortify Analysis') {
    steps {
        glFortifyScan fortifyBuildName: 'myBuild',
            scarProjectName: 'myScarProject',
            javascript: [source: 'dist'],
            isGenerateDevWorkbook: true
    }
}
```

## Javascript 
ES5 is only supported by Fortify.

```groovy
// will attempt to scan all JS files in the dist directory

 stage('Fortify Analysis') {
    steps {
        glFortifyScan fortifyBuildName: 'myBuild',
            scarProjectName: 'myScarProject',
            javascript: [source: 'dist']
    }
}
```

```groovy
// will attempt to scan all JS files in the dist directory and also any html files.

 stage('Fortify Analysis') {
    steps {
        glFortifyScan fortifyBuildName: 'myBuild',
            scarProjectName: 'myScarProject',
            javascript: [source: 'dist', additionalTranslateOptions: '-Dcom.fortify.sca.EnableDOMModeling=true']
    }
}
```