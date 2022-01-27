## Topics:
* [Terraform Language](#terraform-language)
* [ThreadPoolExecutor](#threadpoolexecutor)
* [ForkJoinPool](#forkjoinpool)
* [CompletionService](#completionservice)



### Terraform Language                    
* The main purpose of the Terraform language is declaring resources, which represent infrastructure objects. All other language features exist only to make the definition of resources more flexible and convenient.     
* A Terraform configuration is a complete document in the Terraform language that tells Terraform how to manage a given collection of infrastructure. A configuration can consist of multiple files and directories.        
        
        
        resource "aws_vpc" "main" {     
          cidr_block = var.base_cidr_block      
        }       
                
        <BLOCK TYPE> "<BLOCK LABEL>" "<BLOCK LABEL>" {      
          # Block body      
          <IDENTIFIER> = <EXPRESSION> # Argument        
        }       
                
* **Blocks** are containers for other content and usually represent the configuration of some kind of object, like a resource. Blocks have a block type, can have zero or more labels, and have a body that contains any number of arguments and nested blocks. Most of Terraform's features are controlled by top-level blocks in a configuration file.      
* **Arguments** assign a value to a name. They appear within blocks.      
* **Expressions** represent a value, either literally or by referencing and combining other values. They appear as values for arguments, or within other expressions.     
* The Terraform language is declarative, describing an intended goal rather than the steps to reach that goal. The ordering of blocks and the files they are organized into are generally not significant; Terraform only considers implicit and explicit relationships between resources when determining an order of operations.                


### Files and Directories                    
* A **module** is a collection of .tf and/or .tf.json files kept together in a directory.
* A Terraform module only consists of the top-level configuration files in a directory; nested directories are treated as completely separate modules, and are not automatically included in the configuration.
* Terraform evaluates all of the configuration files in a module, effectively treating the entire module as a single document. Separating various blocks into different files is purely for the convenience of readers and maintainers.
* Terraform always runs in the context of a single root module. A complete Terraform configuration consists of a root module and the tree of child modules (which includes the modules called by the root module, any modules called by those modules, etc.).
    - In Terraform CLI, the root module is the working directory where Terraform is invoked. 
    - n Terraform Cloud and Terraform Enterprise, the root module for a workspace defaults to the top level of the configuration directory.

### Override Files
* In some rare cases, it is convenient to be able to override specific portions of an existing configuration object in a separate file. Terraform has special handling of any configuration file whose name ends in _override.tf or _override.tf.json.    
* Terraform initially skips these override files when loading configuration, and then afterwards processes each one in turn (in lexicographical order). For each top-level block defined in an override file, Terraform attempts to find an already-defined object corresponding to that block and then merges the override block contents into the existing object. [override example](https://www.terraform.io/docs/language/files/override.html) 

* **Resources** are the most important element in the Terraform language. Each resource block describes one or more infrastructure objects, such as virtual networks, compute instances etc.
* **Resource Blocks** documents the syntax for declaring resources. Below resource block declares a resource of a given type ("aws_instance") with a given local name ("web"). The name is used to refer to this resource from elsewhere in the same Terraform module, but has no significance outside that module's scope.
* The resource type and name together serve as an identifier for a given resource and so must be unique within a module.
* Most arguments in this section depend on the resource type, and indeed in this example both ami and instance_type are arguments defined specifically for the aws_instance resource type.


        resource "aws_instance" "web" {
          ami           = "ami-a1b2c3d4"
          instance_type = "t2.micro"
        }

* Each resource type is implemented by a provider, which is a plugin for Terraform that offers a collection of resource types. A provider usually provides resources to manage a single cloud or on-premises infrastructure platform.

* **Resource Behavior** explains in more detail how Terraform handles resource declarations when applying a configuration.
* Applying a Terraform configuration is the process of creating, updating, and destroying real infrastructure objects in order to make their settings match the configuration.
* In summary, applying a Terraform configuration will:
    - Create resources that exist in the configuration but are not associated with a real infrastructure object in the state.
    - Destroy resources that exist in the state but no longer exist in the configuration.
    - Update in-place resources whose arguments have changed.
    - Destroy and re-create resources whose arguments have changed but which cannot be updated in-place due to remote API limitations.
* Expressions within a Terraform module can access information about resources in the same module, and you can use that information to help configure other resources. Use the <RESOURCE TYPE>.<NAME>.<ATTRIBUTE> syntax to reference a resource attribute in an expression.

* The **Meta-Arguments** section documents special arguments that can be used with every resource type, including depends_on, count, for_each, provider, and lifecycle.
* Use the depends_on meta-argument to handle hidden resource or module dependencies that Terraform can't automatically infer.
* count is a meta-argument defined by the Terraform language. It can be used with modules and with every resource type.
  
* The count meta-argument accepts a whole number, and creates that many instances of the resource or module. Each instance has a distinct infrastructure object associated with it, and each is separately created, updated, or destroyed when the configuration is applied.

  
      resource "aws_instance" "server" {
        count = 4 # create four similar EC2 instances
      
        ami           = "ami-a1b2c3d4"
        instance_type = "t2.micro"
      
        tags = {
          Name = "Server ${count.index}"
        }
      }
* The for_each meta-argument accepts a map or a set of strings, and creates an instance for each item in that map or set. Each instance has a distinct infrastructure object associated with it, and each is separately created, updated, or destroyed when the configuration is applied.


        **Map:**
        
        resource "azurerm_resource_group" "rg" {
          for_each = {
            a_group = "eastus"
            another_group = "westus2"
          }
          name     = each.key
          location = each.value
        }

        **Set of strings:**

        resource "aws_iam_user" "the-accounts" {
          for_each = toset( ["Todd", "James", "Alice", "Dottie"] )
          name     = each.key
        } 
        
* The **for_each** value must be known before Terraform performs any remote resource actions. This means for_each can't refer to any resource attributes that aren't known until after a configuration is applied (such as a unique ID generated by the remote API when an object is created).  

* The **provider** meta-argument specifies which provider configuration to use for a resource, overriding Terraform's default behavior of selecting one based on the resource type name. Its value should be an unquoted <PROVIDER>.<ALIAS> reference.


        # default configuration
        provider "google" {
          region = "us-central1"
        }
        
        # alternate configuration, whose alias is "europe"
        provider "google" {
          alias  = "europe"
          region = "europe-west1"
        }
        
        resource "google_compute_instance" "example" {
          # This "provider" meta-argument selects the google provider
          # configuration whose alias is "europe", rather than the
          # default configuration.
          provider = google.europe
        
          # ...
        }

* **lifecycle** is a nested block that can appear within a resource block. The lifecycle block and its contents are meta-arguments, available for all resource blocks regardless of type.

 
        resource "azurerm_resource_group" "example" {
          # ...
        
          lifecycle {
            create_before_destroy = true
          }
        }             

* **Provisioners** documents configuring post-creation actions for a resource using the provisioner and connection blocks. Since provisioners are non-declarative and potentially unpredictable, we strongly recommend that you treat them as a last resort.   

### Data Source     
* Data sources allow Terraform to use information defined outside of Terraform, defined by another separate Terraform configuration, or modified by functions.
* A data block requests that Terraform read from a given data source ("aws_ami") and export the result under the given local name ("example"). The name is used to refer to this resource from elsewhere in the same Terraform module, but has no significance outside of the scope of a module.
* The data source and name together serve as an identifier for a given resource and so must be unique within a module.


        data "aws_ami" "example" {
          most_recent = true
        
          owners = ["self"]
          tags = {
            Name   = "app-server"
            Tested = "true"
          }
        }
        
* When distinguishing from data resources, the primary kind of resource (as declared by a resource block) is known as a managed resource. Both kinds of resources take arguments and export attributes for use in configuration, but while managed resources cause Terraform to create, update, and delete infrastructure objects, data resources cause Terraform only to read objects. 
* Each data instance will export one or more attributes, which can be used in other resources as reference expressions of the form data.<TYPE>.<NAME>.<ATTRIBUTE>. 


        resource "aws_instance" "web" {
          ami           = data.aws_ami.web.id
          instance_type = "t1.micro"
        }

        
        