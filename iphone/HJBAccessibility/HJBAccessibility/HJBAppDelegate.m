//
//  HJBAppDelegate.m
//  HJBAccessibility
//
//  Created by Heath Borders on 8/27/13.
//  Copyright (c) 2013 Heath Borders. All rights reserved.
//

#import "HJBAppDelegate.h"
#import "HJBAccessibilityViewController.h"

@implementation HJBAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor whiteColor];
    self.window.rootViewController = [HJBAccessibilityViewController new];
    [self.window makeKeyAndVisible];
    return YES;
}

@end
