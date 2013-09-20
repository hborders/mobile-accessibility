//
//  HJBAccessibilityView.m
//  HJBAccessibility
//
//  Created by Heath Borders on 8/27/13.
//  Copyright (c) 2013 Heath Borders. All rights reserved.
//

#import "HJBAccessibilityView.h"

@interface HJBAccessibilityView ()

@property (nonatomic) NSArray *accessibilityElements;

@end

@implementation HJBAccessibilityView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}

#pragma mark - UIView

- (void)setFrame:(CGRect)frame {
    [super setFrame:frame];
    
    [self updateAccessibilityElements];
}

- (void)setBounds:(CGRect)bounds {
    [super setBounds:bounds];
    
    [self updateAccessibilityElements];
}

- (void)didMoveToSuperview {
    [super didMoveToSuperview];
    
    [self updateAccessibilityElements];
}

- (void)didMoveToWindow {
    [super didMoveToWindow];
    
    [self updateAccessibilityElements];
}

- (void)drawRect:(CGRect)rect {
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    if ([self.accessibilityElements count] < 4) {
        [[UIColor whiteColor] setFill];
        CGContextFillRect(context, self.bounds);
    } else {
        void (^drawAccessibilityElementWithFillColor)(UIAccessibilityElement *, UIColor *) = ^(UIAccessibilityElement *accessibilityElement, UIColor *fillColor) {
            [fillColor setFill];
            
            CGRect rect = [self convertRect:accessibilityElement.accessibilityFrame
                                   fromView:nil];
            
            CGContextFillRect(context, rect);
        };
        
        drawAccessibilityElementWithFillColor(self.accessibilityElements[0],
                                              [UIColor redColor]);
        drawAccessibilityElementWithFillColor(self.accessibilityElements[1],
                                              [UIColor orangeColor]);
        drawAccessibilityElementWithFillColor(self.accessibilityElements[2],
                                              [UIColor yellowColor]);
        drawAccessibilityElementWithFillColor(self.accessibilityElements[3],
                                              [UIColor greenColor]);
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    for (UITouch *touch in touches) {
        CGPoint locationPoint = [touch locationInView:self];
        CGPoint windowLocationPoint = [self.window convertPoint:locationPoint
                                                       fromView:self];
        
        for (UIAccessibilityElement *accessibilityElement in self.accessibilityElements) {
            if (CGRectContainsPoint(accessibilityElement.accessibilityFrame,
                                    windowLocationPoint)) {
                [[[UIAlertView alloc] initWithTitle:@"HJBAccessibility"
                                            message:[NSString stringWithFormat:@"Tapped %@",
                                                     accessibilityElement.accessibilityLabel]
                                           delegate:nil
                                  cancelButtonTitle:@"OK"
                                  otherButtonTitles:nil] show];
            }
        }
    }
}

#pragma mark - UIAccessibility

- (BOOL)isAccessibilityElement {
    return NO;
}

#pragma mark - UIAccessibilityContainer

- (NSInteger)accessibilityElementCount {
    NSUInteger accessibilityElementCount = [self.accessibilityElements count];
    return accessibilityElementCount;
}

- (id)accessibilityElementAtIndex:(NSInteger)index {
    if (index < [self.accessibilityElements count]) {
        UIAccessibilityElement *accessibilityElement = self.accessibilityElements[index];
        return accessibilityElement;
    } else {
        return nil;
    }
}

- (NSInteger)indexOfAccessibilityElement:(id)element {
    NSUInteger index = [self.accessibilityElements indexOfObject:element];
    return index;
}

#pragma mark - private API

- (void)updateAccessibilityElements {
    CGRect topHalfRect;
    CGRect bottomHalfRect;
    CGRectDivide(self.bounds,
                 &topHalfRect,
                 &bottomHalfRect,
                 floor(CGRectGetHeight(self.bounds) / 2),
                 CGRectMinYEdge);
    
    CGRect topLeftRect;
    CGRect topRightRect;
    CGRectDivide(topHalfRect,
                 &topLeftRect,
                 &topRightRect,
                 floor(CGRectGetWidth(self.bounds) / 2),
                 CGRectMinXEdge);
    
    CGRect bottomLeftRect;
    CGRect bottomRightRect;
    CGRectDivide(bottomHalfRect,
                 &bottomLeftRect,
                 &bottomRightRect,
                 floor(CGRectGetWidth(self.bounds) / 2),
                 CGRectMinXEdge);
    
    CGRect topLeftAccessibilityFrame = [self convertRect:topLeftRect
                                                  toView:nil];
    CGRect topRightAccessibilityFrame = [self convertRect:topRightRect
                                                   toView:nil];
    CGRect bottomLeftAccessibilityFrame = [self convertRect:bottomLeftRect
                                                     toView:nil];
    CGRect bottomRightAccessibilityFrame = [self convertRect:bottomRightRect
                                                      toView:nil];
    
    
    UIAccessibilityElement *topLeftAccessibiltyElement = [[UIAccessibilityElement alloc] initWithAccessibilityContainer:self];
    topLeftAccessibiltyElement.accessibilityLabel = @"Top Left";
    topLeftAccessibiltyElement.accessibilityFrame = topLeftAccessibilityFrame;
    topLeftAccessibiltyElement.accessibilityTraits = UIAccessibilityTraitButton;
    
    UIAccessibilityElement *topRightAccessibiltyElement = [[UIAccessibilityElement alloc] initWithAccessibilityContainer:self];
    topRightAccessibiltyElement.accessibilityLabel = @"Top Right";
    topRightAccessibiltyElement.accessibilityFrame = topRightAccessibilityFrame;
    topRightAccessibiltyElement.accessibilityTraits = UIAccessibilityTraitButton;
    
    UIAccessibilityElement *bottomLeftAccessibiltyElement = [[UIAccessibilityElement alloc] initWithAccessibilityContainer:self];
    bottomLeftAccessibiltyElement.accessibilityLabel = @"Bottom Left";
    bottomLeftAccessibiltyElement.accessibilityFrame = bottomLeftAccessibilityFrame;
    bottomLeftAccessibiltyElement.accessibilityTraits = UIAccessibilityTraitButton;
    
    UIAccessibilityElement *bottomRightAccessibiltyElement = [[UIAccessibilityElement alloc] initWithAccessibilityContainer:self];
    bottomRightAccessibiltyElement.accessibilityLabel = @"Bottom Right";
    bottomRightAccessibiltyElement.accessibilityFrame = bottomRightAccessibilityFrame;
    bottomRightAccessibiltyElement.accessibilityTraits = UIAccessibilityTraitButton;
    
    self.accessibilityElements = (@[
                                  topLeftAccessibiltyElement,
                                  topRightAccessibiltyElement,
                                  bottomLeftAccessibiltyElement,
                                  bottomRightAccessibiltyElement,
                                  ]);
    
    if (self.window) {
        UIAccessibilityPostNotification(UIAccessibilityLayoutChangedNotification,
                                        @"Layout Changed");
    }
}

@end
