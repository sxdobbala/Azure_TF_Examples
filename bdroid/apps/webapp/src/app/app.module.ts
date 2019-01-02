import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import * as Hammer from 'hammerjs';
import { HammerGestureConfig, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { PreloadAllModules, RouterModule } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreModule } from '@bdroid/core';

import { environment } from '@env/environment';
import { PreloadSelectedModulesList } from './preloading-strategy';

export class MyHammerConfig extends HammerGestureConfig {
  overrides = <any>{
    // override hammerjs default configuration
    pan: { direction: Hammer.DIRECTION_ALL },
    swipe: { direction: Hammer.DIRECTION_ALL },
  };
}

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(
      [
        { path: '', redirectTo: 'home', pathMatch: 'full' },
        { path: 'home', loadChildren: '@bdroid/home#HomeModule', data: { preload: true } },
        { path: 'dashboard', loadChildren: '@bdroid/dashboard#DashboardModule', data: { preload: true } },
        { path: '404', loadChildren: '@bdroid/not-found#NotFoundModule', data: { title: '404', preload: false } },
        // 404 should be last
      ],
      {
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
        initialNavigation: 'enabled',
        preloadingStrategy: PreloadAllModules, // TODO: PreloadSelectedModulesList
        paramsInheritanceStrategy: 'always',
        // enableTracing: true, // enable to debug routing during development
        // onSameUrlNavigation: 'reload'
      },
    ),
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production }),
    CoreModule, // IMP: Please keep CoreModule after RouterModule
  ],
  providers: [
    PreloadSelectedModulesList,
    {
      provide: HAMMER_GESTURE_CONFIG,
      useClass: MyHammerConfig,
    },
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent],
})
export class AppModule {}