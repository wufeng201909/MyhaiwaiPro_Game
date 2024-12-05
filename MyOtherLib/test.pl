#!/usr/bin/perl
use strict;
use warnings FATAL => 'all';
use autodie;
use utf8;
use Encode qw(decode encode);
use File::Find;

if(@ARGV ne 3){ # 检查参数
    my $err = <<"err";
 The script execution parameters are wrong! !
 path, "suffix", "old value/new value"
err
    die $err;
}

# path, suffix, reg
my ($path, $suffix, $rp) = @ARGV;
my @suffix = split " ", $suffix; # 记得把字符串，转化为数组

sub search_file{
    my ($fname, $rp) = @_;
    my ($o) = split("/", $rp);
    open of, "<", $fname;
    while(<of>){
        chomp;
        if($_ =~ /$o/){
            return !!1;
        }
    }
    return !!0;
}

sub change_file{
    my ($fname, $rp) = @_; # 获取操作文件名 和 替换的正则
    if( !search_file($fname, $rp) ){ # 不存在关键字直接返回
        return !!0;
    }

    my @data;
    my ($o, $n) = split("/", $rp);
    if(defined $n){#$n有值则表示替换指定字符串
        open of, "<", $fname;
        while(<of>){
            chomp;
            $_ =~ s/$o/$n/;
            push @data, $_;
        }
        open my $wf, "+>", $fname;
        print $wf $_."\n" foreach @data;
    }
    return !!1;
}


sub scan_file{
    my ($fpath) = @_;
    my @files = glob($fpath);
    foreach my $file (@files){
        if(-d $file){ # 文件递归下去
            scan_file("$file/*");
        }elsif(-f $file){
            foreach my $su (@suffix){
                if($file =~ /$su$/){ # 文件后缀在匹配范围
                    if(change_file($file, $rp)){ # 收集受到影响的文件路径
                        print "$file\n";
                    }
                }
            }

        }
    }
}

scan_file($path);
# ------------------以下为查找指定字符串----------------
# print encode("gbk","------------------以下为查找指定字符串----------------\n");
# my $dir_path = $path;
# my $search_string = $rp;
#
# find(\&search_for_string, $dir_path);
#
# sub search_for_string {
#     my $file = $_;
#     if (-f $file) {    # 如果是普通文件
#         foreach my $su (@suffix){
#             if($file =~ /$su$/) { # 文件后缀在匹配范围
#                 open(my $fh, '<', $file);
#                 while (<$fh>) {
#                     if (index($_, $search_string) != -1) {
#                         print "$File::Find::name\n";
#                         last;
#                     }
#                 }
#                 close($fh);
#             }
#         }
#     }
# }