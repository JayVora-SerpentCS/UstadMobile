//
//  UstadBaseUIViewController.m
//  UstadMobileIOS
//
//  This view controller handles the things which are defined in UstadView
//  inc. returning to getContext (returning itself), get/set direction,
//  and a blank method for setUIStrings
//
//  The arguments getter/setter sets the arguments that were given by
//  the core controller that this view probably needs to make it's controller

//
//  Created by Mike Dawson on 16/08/16.
//  Copyright © 2016 UstadMobile FZ-LLC. All rights reserved.
//

#import "UstadBaseUIViewController.h"

@interface UstadBaseUIViewController ()
@property jint direction;
@property JavaUtilHashtable *viewArgs;
@end

@implementation UstadBaseUIViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)setArgumentsWithHashtable:(JavaUtilHashtable *)arguments {
    self.viewArgs = arguments;
}

-(JavaUtilHashtable *)getArguments {
    return self.viewArgs;
}


- (id)getContext {
    return self;
}

- (jint)getDirection {
    return self.direction;
}

- (void)setDirectionWithInt:(jint)dir {
    self.direction = dir;
}

- (void)setAppMenuCommandsWithNSStringArray:(IOSObjectArray *)labels
                               withIntArray:(IOSIntArray *)ids {
    //not implemented yet...
}

- (void)setUIStrings {
    //right now there's no non tab components here with localizable ui strings
}



/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end