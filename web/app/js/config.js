/**
 * INSPINIA - Responsive Admin Theme
 *
 * Inspinia theme use AngularUI Router to manage routing and views
 * Each view are defined as state.
 * Initial there are written state for all view in theme.
 *
 */
function config($stateProvider, $urlRouterProvider, $ocLazyLoadProvider) {
    $urlRouterProvider.otherwise("/index/main");

    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: false
    });

    $stateProvider

        .state('index', {
            abstract: true,
            url: "/index",
            templateUrl: "views/common/content.html",
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            name: 'angular-chartist',
                            files: ['js/plugins/chartist/chartist.min.js', 'css/plugins/chartist/chartist.min.css', 'js/plugins/chartist/angular-chartist.min.js']
                        },
                        {
                            files: ['js/plugins/footable/footable.all.min.js', 'css/plugins/footable/footable.core.css']
                        },
                        {
                            name: 'ui.footable',
                            files: ['js/plugins/footable/angular-footable.js']
                        }
                    ]);
                }
            }
        })
        .state('index.summary', {
            url: "/summary",
            templateUrl: "views/summary.html",
            data: { pageTitle: 'Keywords Summary' }
        })
        .state('index.scenario', {
            url: "/scenario",
            templateUrl: "views/scenario.html",
            data: { pageTitle: 'Keyword Scenario' }
        })
        .state('index.article', {
            url: "/article",
            templateUrl: "views/article.html",
            data: { pageTitle: 'Related Article' }
        })
        .state('index.user', {
            url: "/user",
            templateUrl: "views/user.html",
            data: { pageTitle: 'Related User' }
        })
        .state('index.rule', {
            url: "/rule",
            templateUrl: "views/rule.html",
            data: { pageTitle: 'Configuration Rules' }
        })
}
angular
    .module('inspinia')
    .config(config)
    .run(function($rootScope, $state) {
        $rootScope.$state = $state;
    });
