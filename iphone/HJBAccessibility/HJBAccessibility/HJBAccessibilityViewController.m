//
//  HJBAccessibilityViewController.m
//  HJBAccessibility
//
//  Created by Heath Borders on 8/27/13.
//  Copyright (c) 2013 Heath Borders. All rights reserved.
//

#import "HJBAccessibilityViewController.h"
#import "HJBAccessibilityView.h"

@interface HJBAccessibilityViewController ()

@end

@implementation HJBAccessibilityViewController

- (void)loadView {
    [super loadView];
    
    CGRect buttonFrame;
    CGRect accessibilityViewFrame;
    CGRectDivide(self.view.bounds,
                 &buttonFrame,
                 &accessibilityViewFrame,
                 44,
                 CGRectMinYEdge);
    
    UIButton *button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    button.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth;
    button.frame = buttonFrame;
    [button setTitle:@"Foo"
            forState:UIControlStateNormal];
    [self.view addSubview:button];
    
    HJBAccessibilityView *accessibilityView = [[HJBAccessibilityView alloc] initWithFrame:accessibilityViewFrame];
    accessibilityView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self.view addSubview:accessibilityView];
}

@end
